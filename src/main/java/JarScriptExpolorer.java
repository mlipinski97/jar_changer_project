import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class JarScriptExpolorer {

    ClassPool classPool;
    JarChanger jarChanger;

    public JarScriptExpolorer(JarDisplayer jarDisplayer){
        this.classPool = jarDisplayer.getClassPool();
        this.jarChanger = new JarChanger(this.classPool);
    }

    public ArrayList<CtClass> getAddedClasses(){
        return jarChanger.getAddedClasses();
    }
    public ArrayList<String> getAddedPackages(){
        return jarChanger.getAddedPackages();
    }

    public void execute(ArrayList<String> lineArray){
        switch (lineArray.get(0)){
            case "add-package": //package
                try{
                    addPackage(lineArray);
                }catch (Exception e){
                    System.out.println("Cannot Add Package");
                }
                break;
            case "remove-package": //package
                try{
                    removePackage(lineArray);
                }catch (Exception e){
                    System.out.println("Cannot delete Package");
                }
                break;
            case "add-class": //klasa
                try{
                  addClass(lineArray);
                }catch (Exception e){
                    System.out.println("Cannot Add Class");
                }
                break;
            case "remove-class": //klasa
                try{
                     removeClass(lineArray);
                }catch (Exception e){
                    System.out.println("Cannot delete Class");
                }
                break;
            case "add-interface": //klasa
                try{
                     addInterface(lineArray);
                }catch (Exception e){
                    System.out.println("Cannot Add interface");
                }
                break;
            case "remove-interface": //klasa
                try{
                    removeInterface(lineArray);
                }catch (Exception e){
                    System.out.println("Cannot delete interface");
                }
                break;
            case "add-method": //klasa metoda
                 try{
                    addMethod(lineArray);
                 }catch (Exception e){
                     System.out.println("Cannot Add method");
                 }
                break;
            case "remove-method": //klasa metoda(argumenty)
                try{
                    removeMethod(lineArray);
                }catch (Exception e){
                    System.out.println("Cannot delete method");
                }
                break;
            case "set-method-body": //klasa src to body metoda(argumenty)
                try{
                    setMethodBody(lineArray);
                }catch (Exception e){
                    System.out.println("Cannot set method body");
                }
                break;
            case "add-before-method": //klasa src to body metoda(argumenty)
                try{
                    setMethodBodyBefore(lineArray);
                }catch (Exception e){
                    System.out.println("Cannot alter method body(before)");
                }
                break;
            case "add-after-method": //klasa src to body metoda(argumenty)
                try{
                    setMethodBodyAfter(lineArray);
                }catch (Exception e){
                    System.out.println("Cannot alter method body(after)");
                }
                break;
            case "add-field":  //klasa field
                try{
                    addField(lineArray);
                }catch (Exception e){
                    System.out.println("Cannot add field");
                }
                break;
            case "remove-field":  //klasa field
                try{
                    removeField(lineArray);
                }catch (Exception e){
                    System.out.println("Cannot delete field");
                }
                break;
            case "add-ctor": // klasa (argumenty)
                try{
                    addConstructor(lineArray);
                }catch (Exception e){
                    System.out.println("Cannot add constructor");
                }
                break;
            case "remove-ctor": //klasa (argumenty)
                try{
                    removeConstructor(lineArray);
                }catch (Exception e){
                    System.out.println("Cannot delete constructor");
                }
                break;
            case "set-ctor-body"://klasa src to body (argumenty)
                try{
                    setConstructorBody(lineArray);
                }catch (Exception e){
                    System.out.println("Cannot set constructor body");
                }
                break;
        }

    }

    private void addPackage(ArrayList<String> lineArray) {
        jarChanger.addPackage(lineArray.get(1));
    }

    private void removePackage(ArrayList<String> lineArray) {
        jarChanger.removePackage(lineArray.get(1));
    }

    private void setConstructorBody(ArrayList<String> lineArray) {
        String content  = "";
        try {
            content = new String ( Files.readAllBytes( Paths.get(lineArray.get(2)) ) );
        }
        catch (IOException e) {
            System.out.println("can not open ctr body file");
            e.printStackTrace();
        }
        for (int i = 4; i < lineArray.size(); i++) {
            lineArray.set(3, lineArray.get(3) + lineArray.get(i));
        }
        lineArray.set(3, lineArray.get(3).replace("(", ""));
        lineArray.set(3, lineArray.get(3).replace(")", ""));
        lineArray.set(3, lineArray.get(3).replace(" ", ""));
        ArrayList<String> argArray = new ArrayList<String>(Arrays.asList(lineArray.get(3).split(",")));
        if (argArray.size() == 1 && argArray.get(0).equals("")) {
            jarChanger.setConstructorBody(lineArray.get(1), content, null);
        } else {
            CtClass[] args = createArgsArray(argArray);
            jarChanger.setConstructorBody(lineArray.get(1), content, args);
        }
    }

    private void setMethodBody(ArrayList<String> lineArray) {
        String content  = "";
        try {
            content = new String ( Files.readAllBytes( Paths.get(lineArray.get(2)) ) );
        }
        catch (IOException e) {
            System.out.println("Cannot find method body file");
            e.printStackTrace();
        }
        String methodName = "";
        char[] cArray = lineArray.get(3).toCharArray();
        for (int i = 0; i < cArray.length; i++) {
            if (cArray[i] == '(') {
                break;
            }
            methodName += cArray[i];
        }
        methodName = methodName.trim();
        for (int i = 4; i < lineArray.size(); i++) {
            lineArray.set(3, lineArray.get(3) + lineArray.get(i));
        }
        lineArray.set(3, lineArray.get(3).replace("(", ""));
        lineArray.set(3, lineArray.get(3).replace(")", ""));
        lineArray.set(3, lineArray.get(3).replace(methodName, ""));
        lineArray.set(3, lineArray.get(3).replace(" ", ""));
        ArrayList<String> argArray = new ArrayList<String>(Arrays.asList(lineArray.get(3).split(",")));
        if (argArray.size() == 1 && argArray.get(0).equals("")) {
            jarChanger.setMethodBody(lineArray.get(1), content, methodName, null);
        } else {
            CtClass[] args = createArgsArray(argArray);
            jarChanger.setMethodBody(lineArray.get(1), content, methodName, args);
        }
    }

    private void setMethodBodyAfter(ArrayList<String> lineArray) {
        String content = "";
        try {
            content = new String ( Files.readAllBytes( Paths.get(lineArray.get(2)) ) );
        }
        catch (IOException e) {
            System.out.println("Cannot find method body file");
            e.printStackTrace();
        }
        String methodName = "";
        char[] cArray = lineArray.get(3).toCharArray();
        for (int i = 0; i < cArray.length; i++) {
            if (cArray[i] == '(') {
                break;
            }
            methodName += cArray[i];
        }
        methodName = methodName.trim();
        for (int i = 4; i < lineArray.size(); i++) {
            lineArray.set(3, lineArray.get(3) + lineArray.get(i));
        }
        lineArray.set(3, lineArray.get(3).replace("(", ""));
        lineArray.set(3, lineArray.get(3).replace(")", ""));
        lineArray.set(3, lineArray.get(3).replace(methodName, ""));
        lineArray.set(3, lineArray.get(3).replace(" ", ""));
        ArrayList<String> argArray = new ArrayList<String>(Arrays.asList(lineArray.get(3).split(",")));
        if (argArray.size() == 1 && argArray.get(0).equals("")) {
            jarChanger.setMethodBodyAfter(lineArray.get(1), content, methodName, null);
        } else {
            CtClass[] args = createArgsArray(argArray);
            jarChanger.setMethodBodyAfter(lineArray.get(1), content, methodName, args);
        }
    }
    private void setMethodBodyBefore(ArrayList<String> lineArray) {
        String content = "";
        try {
            content = new String ( Files.readAllBytes( Paths.get(lineArray.get(2)) ) );
        }
        catch (IOException e) {
            System.out.println("Cannot find method body file");
            e.printStackTrace();
        }
        String methodName = "";
        char[] cArray = lineArray.get(3).toCharArray();
        for (int i = 0; i < cArray.length; i++) {
            if (cArray[i] == '(') {
                break;
            }
            methodName += cArray[i];
        }
        methodName = methodName.trim();
        for (int i = 4; i < lineArray.size(); i++) {
            lineArray.set(3, lineArray.get(3) + lineArray.get(i));
        }
        lineArray.set(3, lineArray.get(3).replace("(", ""));
        lineArray.set(3, lineArray.get(3).replace(")", ""));
        lineArray.set(3, lineArray.get(3).replace(methodName, ""));
        lineArray.set(3, lineArray.get(3).replace(" ", ""));
        ArrayList<String> argArray = new ArrayList<String>(Arrays.asList(lineArray.get(3).split(",")));
        if (argArray.size() == 1 && argArray.get(0).equals("")) {
            jarChanger.setMethodBodyBefore(lineArray.get(1), content, methodName, null);
        } else {
            CtClass[] args = createArgsArray(argArray);
            jarChanger.setMethodBodyBefore(lineArray.get(1), content, methodName, args);
        }
    }
    private void addConstructor(ArrayList<String> lineArray) {
        for(int i=3; i<lineArray.size();i++){
            lineArray.set(2, lineArray.get(2)+lineArray.get(i));
        }
        lineArray.set(2, lineArray.get(2).replace("(", ""));
        lineArray.set(2, lineArray.get(2).replace(")", ""));
        lineArray.set(2, lineArray.get(2).replace(" ", ""));
        ArrayList<String> argArray = new ArrayList<String>(Arrays.asList(lineArray.get(2).split(",")));
        if(argArray.size() == 1 && argArray.get(0).equals("")){
            jarChanger.addConstructor(lineArray.get(1), null);
        } else {
            CtClass[] args = createArgsArray(argArray);
            jarChanger.addConstructor(lineArray.get(1), args);
        }
    }

    private void removeConstructor(ArrayList<String> lineArray) {
        for(int i=3; i<lineArray.size();i++){
            lineArray.set(2, lineArray.get(2)+lineArray.get(i));
        }
        lineArray.set(2, lineArray.get(2).replace("(", ""));
        lineArray.set(2, lineArray.get(2).replace(")", ""));
        lineArray.set(2, lineArray.get(2).replace(" ", ""));
        ArrayList<String> argArray = new ArrayList<String>(Arrays.asList(lineArray.get(2).split(",")));
        if(argArray.size() == 1 && argArray.get(0).equals("")){
            jarChanger.deleteConstructor(lineArray.get(1), null);
        } else {
            CtClass[] args = createArgsArray(argArray);
            jarChanger.deleteConstructor(lineArray.get(1), args);
        }
    }

    private void removeField(ArrayList<String> lineArray) {
        String fieldName = "";
        for(int iterator = 2; iterator < lineArray.size(); iterator++){
            fieldName += lineArray.get(iterator) + " ";
        }
        jarChanger.deleteField(lineArray.get(1), fieldName);
    }

    private void addField(ArrayList<String> lineArray) {
        String fieldName = "";
        for(int iterator = 2; iterator < lineArray.size(); iterator++){
            fieldName += lineArray.get(iterator) + " ";
        }
        jarChanger.addField(lineArray.get(1), fieldName);
    }

    private void addInterface(ArrayList<String> lineArray) {
        jarChanger.addInterface(lineArray.get(1));
    }

    private void removeInterface(ArrayList<String> lineArray) {
        jarChanger.deleteInterface(lineArray.get(1));
    }

    private void removeClass(ArrayList<String> lineArray) {
        jarChanger.deleteClass(lineArray.get(1));
    }

    private void addClass(ArrayList<String> lineArray) {
        jarChanger.addClass(lineArray.get(1));
    }

    private void addMethod(ArrayList<String> lineArray){
        String methodName = "";
        String returnType = lineArray.get(4);
        for(int iterator = 2; iterator < lineArray.size(); iterator++){
            methodName += lineArray.get(iterator) + " ";
        }
        jarChanger.addMethod(lineArray.get(1), methodName, returnType);
    }

    private void removeMethod(ArrayList<String> lineArray){
        String methodName = "";
        char[] cArray = lineArray.get(2).toCharArray();
        for(int i=0; i<cArray.length; i++){
            if(cArray[i] == '('){
                break;
            }
            methodName += cArray[i];
        }
        methodName = methodName.trim();
        for(int i=3; i<lineArray.size();i++){
            lineArray.set(2, lineArray.get(2)+lineArray.get(i));
        }
        lineArray.set(2, lineArray.get(2).replace("(", ""));
        lineArray.set(2, lineArray.get(2).replace(")", ""));
        lineArray.set(2, lineArray.get(2).replace(methodName, ""));
        lineArray.set(2, lineArray.get(2).replace(" ", ""));
        ArrayList<String> argArray = new ArrayList<String>(Arrays.asList(lineArray.get(2).split(",")));
        if(argArray.size() == 1 && argArray.get(0).equals("")){
            jarChanger.deleteMethod(lineArray.get(1), methodName, null);
        } else{
            CtClass[] args = createArgsArray(argArray);
            jarChanger.deleteMethod(lineArray.get(1), methodName, args);
        }
    }
    private CtClass[] createArgsArray(ArrayList<String> argArray){
        CtClass[] args = new CtClass[argArray.size()];
        for (int i=0; i<argArray.size(); i++){
            try {
                args[i] = classPool.get(argArray.get(i));
            } catch (NotFoundException e) {
                System.out.println("No such class: " + argArray.get(i));
                e.printStackTrace();
            }
        }
        return args;
    }
}
