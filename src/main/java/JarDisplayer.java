import javassist.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class JarDisplayer {

    private JarFile jarFile;
    private ClassPool classPool;


    public JarDisplayer(String jarPath){
        this.classPool = ClassPool.getDefault();
        try {
            File file = new File(jarPath);
            this.jarFile = new JarFile(file);
            this.classPool.insertClassPath(jarPath);
        }
        catch(Exception e) {
            System.out.println("Error opening input jarfile");
            e.printStackTrace();
        }
    }

    public ClassPool getClassPool() {
        return classPool;
    }
    public JarFile getJarFile(){
        return jarFile;
    }

    public void clean(){
        try{
            if(jarFile != null){
                jarFile.close();
            }
        } catch (IOException e){
            System.out.println("error closing jarFile!");
            e.printStackTrace();
        }
    }

    public void listPackages(){
        for(JarEntry e : Collections.list(jarFile.entries())){
            if (e.isDirectory()){
                System.out.println(e.getName());
            }
        }
    }
    public void listClasses(){
        for(JarEntry e : Collections.list(jarFile.entries())){
            if (e.getName().endsWith(".class")){
                System.out.println(e.getName().replace("/","."));
            }
        }
    }
    public void listMethods(String classPath){
        try {
            classPath = classPath.replace('/', '.');
            CtClass clazz = classPool.get(classPath.substring(0, classPath.lastIndexOf(".")));

            for (CtMethod declaredMethod : clazz.getDeclaredMethods()) {
                System.out.println(declaredMethod.getLongName());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("cannot list methods of class: " + classPath);
        }
    }
    public void listConstructors(String classPath){
        try {
            classPath = classPath.replace('/', '.');
            CtClass clazz = classPool.get(classPath.substring(0, classPath.lastIndexOf(".")));

            for (CtConstructor declaredConstructor : clazz.getDeclaredConstructors()) {
                System.out.println(declaredConstructor.getLongName());
            }
        }
        catch(Exception e) {
            System.out.println("cannot list ctors");
            e.printStackTrace();
        }
    }
    public void listFields(String classPath){
        try {
            classPath = classPath.replace('/', '.');
            CtClass clazz = classPool.get(classPath.substring(0, classPath.lastIndexOf(".")));

            for (CtField declaredField : clazz.getDeclaredFields()) {
                System.out.println(declaredField.getName());
            }
        }
        catch(Exception e) {
            System.out.println("cannot list fields");
            e.printStackTrace();
        }
    }
}
