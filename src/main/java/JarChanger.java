import javassist.*;
import java.util.ArrayList;


public class JarChanger {

    private ClassPool classPool;
    private ArrayList<CtClass> addedClasses;
    private ArrayList<String> addedPackages;

    public JarChanger(ClassPool classPool){
        this.classPool = classPool;
        addedClasses = new ArrayList<>();
        addedPackages = new ArrayList<>();
    }

    public ArrayList<CtClass> getAddedClasses(){
        return this.addedClasses;
    }
    public ArrayList<String> getAddedPackages(){
        return this.addedPackages;
    }

    public void addClass(String classPath){
        CtClass clazz = classPool.makeClass(classPath.substring(0, classPath.lastIndexOf(".")));
        if(!addedClasses.contains(clazz)){
            addedClasses.add(clazz);
            System.out.println("Class " + classPath + " added");
        }else{
            System.out.println("Class " + classPath + " not added duplicate found");
        }
    }
    public void deleteClass(String classPath){ ;
        classPath = classPath.replace('.', '/');
        CtClass clazz = classPool.makeClass(classPath.substring(0, classPath.lastIndexOf("/")));
        for(int i=0; i<addedClasses.size(); i++){
            if(addedClasses.get(i).getName().equals(clazz.getName())){
                addedClasses.remove(i);
                System.out.println("Class " + classPath + " deleted");
            }
        }
    }
    public void addInterface(String classPath){
        CtClass clazz = classPool.makeInterface(classPath.substring(0, classPath.lastIndexOf(".")));
        if(!addedClasses.contains(clazz)){
            addedClasses.add(clazz);
            System.out.println("Interface " + classPath + " added");
        }else{
            System.out.println("Interface " + classPath + " not added duplicate found");
        }
    }
    public void deleteInterface(String classPath){
        classPath = classPath.replace('.', '/');
        CtClass clazz = classPool.makeInterface(classPath.substring(0, classPath.lastIndexOf("/")));
        for(int i=0; i<addedClasses.size(); i++) {
            if (addedClasses.get(i).getName().equals(clazz.getName())) {
                addedClasses.remove(i);
                System.out.println("Interface " + classPath + " removed");
            }
        }
    }
    public void addMethod(String classPath, String methodName, String returnType){
        methodName = methodName.trim();
        if(!returnType.equals("void")){
            methodName += "{ return null;}";
        } else{
            methodName += "{}";
        }
        classPath = classPath.replace('/', '.');
        try{
            CtClass clazz = classPool.get(classPath.substring(0, classPath.lastIndexOf(".")));
            CtMethod m = CtNewMethod.make(methodName, clazz);
            clazz.addMethod(m);
            System.out.println("Method added");
        } catch(NotFoundException nfe){
            for(CtClass ctc : addedClasses){
                if(classPath.equals(ctc.getName().replace('/', '.') + ".class")){
                    try {
                        System.out.println("method added to class to be added");
                        ctc.defrost();
                        CtMethod m = CtNewMethod.make(methodName, ctc);
                        ctc.addMethod(m);
                    } catch (CannotCompileException e) {
                        System.out.println("Can add method");
                    }
                }
            }
        } catch(CannotCompileException cce){
            System.out.println("CAN'T ADD METHOD");
        }
    }
    public void deleteMethod(String classPath, String methodName, CtClass[] args){
        classPath = classPath.replace('/', '.');
        try{
            CtClass clazz = classPool.get(classPath.substring(0, classPath.lastIndexOf(".")));
            CtMethod ctm;
            if(args != null){
                ctm = clazz.getDeclaredMethod(methodName, args);
            }else {
                ctm = clazz.getDeclaredMethod(methodName);
            }
            clazz.removeMethod(ctm);
            System.out.println("Method deleted");
        } catch(NotFoundException nfe){
            for(CtClass ctc : addedClasses){
                if(classPath.equals(ctc.getName().replace('/', '.') + ".class")){
                    try {
                        ctc.defrost();
                        CtMethod ctm;
                        if(args != null){
                            ctm = ctc.getDeclaredMethod(methodName, args);
                        }else {
                            ctm = ctc.getDeclaredMethod(methodName);
                        }
                        ctc.removeMethod(ctm);
                        System.out.println("method removed from class to be added");
                    } catch (NotFoundException e) {
                        System.out.println("Can not delete method!");
                    }
                }
            }
        }
    }

    public void addField(String classPath, String fieldName) {
        fieldName = fieldName.trim();
        classPath = classPath.replace('/', '.');
        try{
            CtClass clazz = classPool.get(classPath.substring(0, classPath.lastIndexOf(".")));
            CtField sampleField = CtField.make(fieldName, clazz);
            clazz.addField(sampleField);
            System.out.println("Field Added");
        } catch(NotFoundException nfe){
            for(CtClass ctc : addedClasses){
                if(classPath.equals(ctc.getName().replace('/', '.') + ".class")){
                    try {
                        System.out.println("field added to class to be added");
                        ctc.defrost();
                        CtField sampleField = CtField.make(fieldName, ctc);
                        ctc.addField(sampleField);
                    } catch (CannotCompileException e) {
                        System.out.println("CAN'T ADD FIELD");
                    }
                }
            }
        } catch(CannotCompileException cce){
            System.out.println("CAN'T ADD FIELD");
        }

    }

    public void deleteField(String classPath, String fieldName){
        fieldName = fieldName.trim();
        classPath = classPath.replace('/', '.');
        try{
            CtClass clazz = classPool.get(classPath.substring(0, classPath.lastIndexOf(".")));
            CtField sampleField = clazz.getDeclaredField(fieldName);
            clazz.removeField(sampleField);
            System.out.println("Field Deleted");
        } catch(NotFoundException nfe){
            for(CtClass ctc : addedClasses){
                if(classPath.equals(ctc.getName().replace('/', '.') + ".class")){
                    try {
                        ctc.defrost();
                        CtField sampleField = ctc.getDeclaredField(fieldName);
                        ctc.removeField(sampleField);
                        System.out.println("field removed from class to be added");
                    } catch (NotFoundException e) {
                        System.out.println("Can not delete field");
                    }
                }
            }
        }
    }

    public void addConstructor(String classPath, CtClass[] args){
        String cstrBody = "{}";
        classPath = classPath.replace('/', '.');
        try{
            CtClass clazz = classPool.get(classPath.substring(0, classPath.lastIndexOf(".")));
            CtConstructor constructor = CtNewConstructor.make(args,null, cstrBody, clazz);
            clazz.addConstructor(constructor);
            System.out.println("Constructor Added");
        } catch(NotFoundException nfe){
            for(CtClass ctc : addedClasses){
                if(classPath.equals(ctc.getName().replace('/', '.') + ".class")){
                    try {
                        System.out.println("constructor added to class to be added");
                        ctc.defrost();
                        CtConstructor constructor = CtNewConstructor.make(args,null, cstrBody, ctc);
                        ctc.addConstructor(constructor);
                    } catch (CannotCompileException e) {
                        System.out.println("CAN'T ADD CONSTRUCTOR");
                    }
                }
            }
        } catch(CannotCompileException cce){
            System.out.println("CAN'T ADD CONSTRUCTOR");
        }
    }
    public void deleteConstructor(String classPath, CtClass[] args){
        classPath = classPath.replace('/', '.');
        try{
            CtClass clazz = classPool.get(classPath.substring(0, classPath.lastIndexOf(".")));
            CtConstructor ctr;
            ctr = clazz.getDeclaredConstructor(args);
            clazz.removeConstructor(ctr);
            System.out.println("Constructor Deleted");
        } catch(NotFoundException nfe){
            for(CtClass ctc : addedClasses){
                if(classPath.equals(ctc.getName().replace('/', '.') + ".class")){
                    try {
                        ctc.defrost();
                        CtConstructor ctr;
                        ctr = ctc.getDeclaredConstructor(args);
                        ctc.removeConstructor(ctr);
                        System.out.println("constructor removed from class to be added");
                    } catch (NotFoundException e) {
                        System.out.println("Can not delete constructor");
                    }
                }
            }
        }
    }

    public void setMethodBody(String classPath, String methodBody, String methodName, CtClass[] args){
        classPath = classPath.replace('/', '.');
        try{
            CtClass clazz = classPool.get(classPath.substring(0, classPath.lastIndexOf(".")));
            CtMethod ctm;
            if(args != null){
                ctm = clazz.getDeclaredMethod(methodName, args);
            }else {
                ctm = clazz.getDeclaredMethod(methodName, null);
            }
            try {
                clazz.removeMethod(ctm);
                ctm.setBody(methodBody);
                clazz.addMethod(ctm);
                System.out.println("Method Body altered");
            } catch (CannotCompileException e) {
                System.out.println("CANNOT ALTER METHOD(SET)");
            }
        } catch(NotFoundException nfe){
            for(CtClass ctc : addedClasses){
                 if(classPath.equals(ctc.getName().replace('/', '.') + ".class")){
                    try {
                        ctc.defrost();
                        CtMethod ctm;
                        if(args != null){
                            ctm = ctc.getDeclaredMethod(methodName, args);
                        }else {
                            ctm = ctc.getDeclaredMethod(methodName);
                        }
                        try {
                            ctc.removeMethod(ctm);
                            ctm.setBody(methodBody);
                            ctc.addMethod(ctm);
                        } catch (CannotCompileException e) {
                        }
                        System.out.println("method altered in class to be added");
                    } catch (NotFoundException e) {
                        System.out.println("Can not set body method");
                    }
                }
            }
        }
    }
    public void setMethodBodyBefore(String classPath, String methodBody, String methodName, CtClass[] args){
        classPath = classPath.replace('/', '.');
        try{
            CtClass clazz = classPool.get(classPath.substring(0, classPath.lastIndexOf(".")));
            CtMethod ctm;
            if(args != null){
                ctm = clazz.getDeclaredMethod(methodName, args);
            }else {
                ctm = clazz.getDeclaredMethod(methodName, null);
            }
            try {
                clazz.removeMethod(ctm);
                ctm.insertBefore(methodBody);
                clazz.addMethod(ctm);
                System.out.println("Method Altered(before)");
            } catch (CannotCompileException e) {
                System.out.println("CANNOT ALTER METHOD(BEFORE)");
            }
        } catch(NotFoundException nfe){
            for(CtClass ctc : addedClasses){
                if(classPath.equals(ctc.getName().replace('/', '.') + ".class")){
                    try {
                        ctc.defrost();
                        CtMethod ctm;
                        if(args != null){
                            ctm = ctc.getDeclaredMethod(methodName, args);
                        }else {
                            ctm = ctc.getDeclaredMethod(methodName);
                        }
                        try {
                            ctc.removeMethod(ctm);
                            ctm.insertBefore(methodBody);
                            ctc.addMethod(ctm);
                        } catch (CannotCompileException e) {
                        }
                        System.out.println("method altered before in class to be added");
                    } catch (NotFoundException e) {
                        System.out.println("Can not set body method(before)");
                    }
                }
            }
        }
    }
    public void setMethodBodyAfter(String classPath, String methodBody, String methodName, CtClass[] args){
        classPath = classPath.replace('/', '.');
        try{
            CtClass clazz = classPool.get(classPath.substring(0, classPath.lastIndexOf(".")));
            CtMethod ctm;
            if(args != null){
                ctm = clazz.getDeclaredMethod(methodName, args);
            }else {
                ctm = clazz.getDeclaredMethod(methodName, null);
            }
            try {
                clazz.removeMethod(ctm);
                ctm.insertAfter(methodBody);
                clazz.addMethod(ctm);
                System.out.println("Method Altered(after)");
            } catch (CannotCompileException e) {
                System.out.println("Can not alter method(after)");
            }
        } catch(NotFoundException nfe){
            for(CtClass ctc : addedClasses){
                if(classPath.equals(ctc.getName().replace('/', '.') + ".class")){
                    try {
                        ctc.defrost();
                        CtMethod ctm;
                        if(args != null){
                            ctm = ctc.getDeclaredMethod(methodName, args);
                        }else {
                            ctm = ctc.getDeclaredMethod(methodName);
                        }
                        try {
                            ctc.removeMethod(ctm);
                            ctm.insertAfter(methodBody);
                            ctc.addMethod(ctm);
                        } catch (CannotCompileException e) {
                            System.out.println("Can not set method(after)");
                        }
                        System.out.println("method altered after in class to be added");
                    } catch (NotFoundException e) {
                        System.out.println("Can not set body method(after)");
                    }
                }
            }
        }
    }
    public void setConstructorBody(String classPath, String methodBody, CtClass[] args){
        classPath = classPath.replace('/', '.');
        try{
            CtClass clazz = classPool.get(classPath.substring(0, classPath.lastIndexOf(".")));
            CtConstructor ctr;
            if(args != null){
                ctr = clazz.getDeclaredConstructor(args);
            }else {
                ctr = clazz.getDeclaredConstructor(null);
            }
            try {
                clazz.removeConstructor(ctr);
                ctr.setBody(methodBody);
                clazz.addConstructor(ctr);
                System.out.println("Constructor Body altered");
            } catch (CannotCompileException e) {
                System.out.println("Can not set constructor body");
            }
        } catch(NotFoundException nfe){
            for(CtClass ctc : addedClasses){
                if(classPath.equals(ctc.getName().replace('/', '.') + ".class")){
                    try {
                        ctc.defrost();
                        CtConstructor ctr;
                        if(args != null){
                            ctr = ctc.getDeclaredConstructor(args);
                        }else {
                            ctr = ctc.getDeclaredConstructor(null);
                        }
                        try {
                            ctc.removeConstructor(ctr);
                            ctr.setBody(methodBody);
                            ctc.addConstructor(ctr);
                        } catch (CannotCompileException e) {
                            System.out.println("Can not set constructor body");
                        }
                        System.out.println("constructor body altered in class to be added");
                    } catch (NotFoundException e) {
                        System.out.println("Can not set constructor body");
                    }
                }
            }
        }
    }
    public void addPackage(String packagePath){
        if(!addedPackages.contains(packagePath)){
            addedPackages.add(packagePath);
            System.out.println("Package added");
        }
    }
    public void removePackage(String packagePath) {
        addedPackages.remove(packagePath);
    }

}
