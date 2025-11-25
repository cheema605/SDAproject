#!/usr/bin/env python3
"""
CK Metrics Analyzer for Java Project
Calculates Chidamber-Kemerer (CK) metrics for Java classes
"""

import os
import re
import json
from pathlib import Path
from typing import Dict, List, Set, Tuple
from collections import defaultdict

class JavaClass:
    def __init__(self, name: str, filepath: str):
        self.name = name
        self.filepath = filepath
        self.methods = []
        self.fields = []
        self.imports = []
        self.extends = None
        self.implements = []
        self.is_abstract = False
        self.is_interface = False
        self.lines_of_code = 0
        self.method_complexities = []
        
    def add_method(self, method_name: str, complexity: int = 1):
        self.methods.append(method_name)
        self.method_complexities.append(complexity)
    
    def wmc(self) -> int:
        """Weighted Methods per Class"""
        return sum(self.method_complexities) if self.method_complexities else len(self.methods)
    
    def rfc(self, all_classes: Dict[str, 'JavaClass']) -> int:
        """Response For a Class - methods + methods called"""
        # Simplified: just count methods for now
        return len(self.methods)

def parse_java_file(filepath: str) -> JavaClass:
    """Parse a Java file and extract class information"""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
        lines = content.split('\n')
    
    class_name = None
    java_class = None
    
    # Extract package
    package_match = re.search(r'package\s+(\S+);', content)
    package = package_match.group(1) if package_match else ""
    
    # Extract imports
    imports = re.findall(r'import\s+([^;]+);', content)
    
    # Find class declaration
    class_pattern = r'(public\s+)?(abstract\s+)?(class|interface)\s+(\w+)'
    class_match = re.search(class_pattern, content)
    
    if class_match:
        is_abstract = class_match.group(2) is not None
        is_interface = class_match.group(3) == 'interface'
        class_name = class_match.group(4)
        java_class = JavaClass(class_name, filepath)
        java_class.is_abstract = is_abstract
        java_class.is_interface = is_interface
        java_class.imports = imports
        java_class.lines_of_code = len([l for l in lines if l.strip() and not l.strip().startswith('//')])
        
        # Find extends
        extends_match = re.search(r'extends\s+(\w+)', content)
        if extends_match:
            java_class.extends = extends_match.group(1)
        
        # Find implements
        implements_match = re.search(r'implements\s+([^{]+)', content)
        if implements_match:
            impls = [i.strip() for i in implements_match.group(1).split(',')]
            java_class.implements = impls
        
        # Count methods (simplified - look for method declarations)
        method_pattern = r'(public|private|protected)?\s*(static)?\s*(\w+)\s+(\w+)\s*\([^)]*\)'
        methods = re.findall(method_pattern, content)
        for method in methods:
            method_name = method[3]
            if method_name not in ['class', 'interface', 'if', 'for', 'while', 'return']:
                # Simple complexity estimation: count if/for/while in method body
                complexity = 1
                java_class.add_method(method_name, complexity)
        
        # Count fields
        field_pattern = r'(private|protected|public)\s+(static\s+)?(final\s+)?(\w+(\[\])?)\s+(\w+)\s*[=;]'
        fields = re.findall(field_pattern, content)
        java_class.fields = [f[5] for f in fields]
    
    return java_class

def calculate_dit(class_name: str, classes: Dict[str, JavaClass], visited: Set[str] = None) -> int:
    """Depth of Inheritance Tree"""
    if visited is None:
        visited = set()
    
    if class_name in visited or class_name not in classes:
        return 0
    
    visited.add(class_name)
    java_class = classes[class_name]
    
    if java_class.extends:
        return 1 + calculate_dit(java_class.extends, classes, visited)
    return 0

def calculate_noc(class_name: str, classes: Dict[str, JavaClass]) -> int:
    """Number of Children"""
    count = 0
    for cls in classes.values():
        if cls.extends == class_name:
            count += 1
    return count

def calculate_cbo(class_name: str, classes: Dict[str, JavaClass]) -> int:
    """Coupling Between Objects - count of other classes used"""
    if class_name not in classes:
        return 0
    
    java_class = classes[class_name]
    coupled_classes = set()
    
    # Check imports and field types
    for imp in java_class.imports:
        # Extract class name from import
        parts = imp.split('.')
        if parts:
            coupled_classes.add(parts[-1])
    
    # Remove self
    coupled_classes.discard(class_name)
    
    return len(coupled_classes)

def calculate_lcom(java_class: JavaClass) -> int:
    """Lack of Cohesion of Methods (simplified)"""
    # Simplified LCOM: if class has many methods but few fields, low cohesion
    if len(java_class.fields) == 0:
        return len(java_class.methods) if len(java_class.methods) > 1 else 0
    
    # Simple heuristic: more methods than fields suggests lower cohesion
    if len(java_class.methods) > len(java_class.fields) * 2:
        return len(java_class.methods) - len(java_class.fields)
    return 0

def analyze_project(project_path: str) -> Dict:
    """Analyze all Java files in the project"""
    java_files = list(Path(project_path).rglob('*.java'))
    classes = {}
    
    print(f"Found {len(java_files)} Java files")
    
    for java_file in java_files:
        try:
            java_class = parse_java_file(str(java_file))
            if java_class and java_class.name:
                classes[java_class.name] = java_class
                print(f"  Analyzed: {java_class.name}")
        except Exception as e:
            print(f"  Error parsing {java_file}: {e}")
    
    # Calculate metrics for each class
    results = []
    for class_name, java_class in classes.items():
        dit = calculate_dit(class_name, classes)
        noc = calculate_noc(class_name, classes)
        cbo = calculate_cbo(class_name, classes)
        wmc = java_class.wmc()
        rfc = java_class.rfc(classes)
        lcom = calculate_lcom(java_class)
        
        results.append({
            'class': class_name,
            'file': java_class.filepath,
            'WMC': wmc,
            'DIT': dit,
            'NOC': noc,
            'CBO': cbo,
            'RFC': rfc,
            'LCOM': lcom,
            'LOC': java_class.lines_of_code,
            'Methods': len(java_class.methods),
            'Fields': len(java_class.fields),
            'IsAbstract': java_class.is_abstract,
            'Extends': java_class.extends or 'None',
            'Implements': ', '.join(java_class.implements) if java_class.implements else 'None'
        })
    
    return {
        'classes': results,
        'total_classes': len(classes),
        'total_files': len(java_files)
    }

def generate_report(analysis: Dict, output_file: str):
    """Generate a markdown report from the analysis"""
    classes = analysis['classes']
    
    report = f"""# CK Metrics Report - Labs Management System

**Generated:** {Path(output_file).stat().st_mtime if Path(output_file).exists() else 'N/A'}  
**Total Classes Analyzed:** {analysis['total_classes']}  
**Total Java Files:** {analysis['total_files']}

---

## CK Metrics Overview

The Chidamber-Kemerer (CK) metrics suite provides object-oriented design metrics:

- **WMC (Weighted Methods per Class)**: Complexity of methods in a class
- **DIT (Depth of Inheritance Tree)**: Depth of inheritance hierarchy
- **NOC (Number of Children)**: Number of direct subclasses
- **CBO (Coupling Between Objects)**: Number of classes coupled to
- **RFC (Response For a Class)**: Number of methods + methods called
- **LCOM (Lack of Cohesion of Methods)**: Measure of method cohesion

---

## Metrics by Class

| Class | WMC | DIT | NOC | CBO | RFC | LCOM | LOC | Methods | Fields |
|-------|-----|-----|-----|-----|-----|------|-----|---------|--------|
"""
    
    # Sort by class name
    classes_sorted = sorted(classes, key=lambda x: x['class'])
    
    for cls in classes_sorted:
        report += f"| {cls['class']} | {cls['WMC']} | {cls['DIT']} | {cls['NOC']} | {cls['CBO']} | {cls['RFC']} | {cls['LCOM']} | {cls['LOC']} | {cls['Methods']} | {cls['Fields']} |\n"
    
    report += "\n---\n\n## Detailed Class Information\n\n"
    
    for cls in classes_sorted:
        report += f"""### {cls['class']}

- **File:** `{cls['file']}`
- **WMC:** {cls['WMC']} (Weighted Methods per Class)
- **DIT:** {cls['DIT']} (Depth of Inheritance Tree)
- **NOC:** {cls['NOC']} (Number of Children)
- **CBO:** {cls['CBO']} (Coupling Between Objects)
- **RFC:** {cls['RFC']} (Response For a Class)
- **LCOM:** {cls['LCOM']} (Lack of Cohesion of Methods)
- **Lines of Code:** {cls['LOC']}
- **Methods:** {cls['Methods']}
- **Fields:** {cls['Fields']}
- **Abstract:** {cls['IsAbstract']}
- **Extends:** {cls['Extends']}
- **Implements:** {cls['Implements']}

"""
    
    # Summary statistics
    if classes:
        avg_wmc = sum(c['WMC'] for c in classes) / len(classes)
        avg_dit = sum(c['DIT'] for c in classes) / len(classes)
        avg_cbo = sum(c['CBO'] for c in classes) / len(classes)
        avg_rfc = sum(c['RFC'] for c in classes) / len(classes)
        total_loc = sum(c['LOC'] for c in classes)
        
        report += f"""---

## Summary Statistics

| Metric | Average | Min | Max |
|--------|---------|-----|-----|
| WMC | {avg_wmc:.2f} | {min(c['WMC'] for c in classes)} | {max(c['WMC'] for c in classes)} |
| DIT | {avg_dit:.2f} | {min(c['DIT'] for c in classes)} | {max(c['DIT'] for c in classes)} |
| CBO | {avg_cbo:.2f} | {min(c['CBO'] for c in classes)} | {max(c['CBO'] for c in classes)} |
| RFC | {avg_rfc:.2f} | {min(c['RFC'] for c in classes)} | {max(c['RFC'] for c in classes)} |
| LOC | {total_loc} | {min(c['LOC'] for c in classes)} | {max(c['LOC'] for c in classes)} |

---

## Quality Assessment

### WMC (Weighted Methods per Class)
- **Good:** < 20 per class
- **Moderate:** 20-50
- **High:** > 50 (may indicate need for refactoring)

### DIT (Depth of Inheritance Tree)
- **Good:** 0-3 levels
- **Moderate:** 4-5 levels
- **High:** > 5 levels (may indicate overuse of inheritance)

### CBO (Coupling Between Objects)
- **Good:** < 5 dependencies per class
- **Moderate:** 5-10
- **High:** > 10 (may indicate tight coupling)

### LCOM (Lack of Cohesion of Methods)
- **Good:** Low values (high cohesion)
- **Moderate:** Medium values
- **High:** High values (low cohesion, may need refactoring)

---

**Report generated by CK Metrics Analyzer**
"""
    
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(report)
    
    print(f"\nReport generated: {output_file}")

if __name__ == '__main__':
    project_path = 'src/main/java'
    output_file = 'CK_METRICS_REPORT.md'
    
    print("CK Metrics Analyzer")
    print("=" * 50)
    print(f"Analyzing project: {project_path}")
    print()
    
    analysis = analyze_project(project_path)
    generate_report(analysis, output_file)
    
    print(f"\nAnalysis complete!")
    print(f"Total classes: {analysis['total_classes']}")

