import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

public class JarPacker {
    private ArrayList<CtClass> addedClasses;
    private ArrayList<String> addedPackages;
    private ArrayList<CtClass> modifiedClasses;
    private JarFile inputJar;
    JarOutputStream jarOutputStream;
    JarInputStream jarInputStream;
    ClassPool classPool;

    public JarPacker(ArrayList<CtClass> addedClasses, ArrayList<String> addedPackages,
                     JarFile inputJar, String outputFileName, JarInputStream jarInputStream, ClassPool classPool){
        this.addedClasses = addedClasses;
        this.addedPackages = addedPackages;
        modifiedClasses = new ArrayList<>();
        this.inputJar = inputJar;
        this.classPool = classPool;
        this.jarInputStream = jarInputStream;
        System.out.println("created new file at: " + outputFileName);
        File file = new File(outputFileName);
        try {
            jarOutputStream = new JarOutputStream(new FileOutputStream(file),jarInputStream.getManifest());
        } catch (IOException e) {
            System.out.println("Cannot create JarOutputStream");
            e.printStackTrace();
        }
    }
    public void pack(){
        try{
            JarEntry jarEntry = null;
            if(addedPackages!=null){
                for (String packagePath : addedPackages)
                {
                    JarEntry newPackage = new JarEntry(packagePath + "/");
                    jarOutputStream.putNextEntry(newPackage);
                }
            }
            for (CtClass i : addedClasses)
            {
                JarEntry jarEntry2 = new JarEntry(i.getName().replaceAll("\\.","/") + ".class");
                byte[] classBytes = i.toBytecode();
                jarOutputStream.putNextEntry(jarEntry2);
                jarOutputStream.write(classBytes);
            }
            while ((jarEntry = jarInputStream.getNextJarEntry())!=null)
            {
                if (jarEntry.getName().endsWith(".class")){
                    String classPath = jarEntry.getName().replace('/', '.');
                    CtClass clazz = classPool.get(classPath.substring(0, classPath.lastIndexOf(".")));
                    modifiedClasses.add(clazz);
                    continue;
                }
                InputStream inputStream = inputJar.getInputStream(jarEntry);
                jarOutputStream.putNextEntry(new JarEntry(jarEntry.getName()));
                byte[] bytes = new byte[2048];
                int bytesRead = 0;
                while((bytesRead = inputStream.read(bytes))!= -1)
                {
                    jarOutputStream.write(bytes,0,bytesRead);
                }
                inputStream.close();
                jarOutputStream.flush();
                jarOutputStream.closeEntry();
            }
            for (CtClass i : modifiedClasses)
            {
                JarEntry jarEntry2 = new JarEntry(i.getName().replaceAll("\\.","/") + ".class");
                byte[] classBytes = i.toBytecode();
                jarOutputStream.putNextEntry(jarEntry2);
                jarOutputStream.write(classBytes);
            }
        } catch (IOException | CannotCompileException exep){
            System.out.println("Problem creating new jar file");
            exep.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally {
            try
            {
                jarInputStream.close();
                jarOutputStream.close();
            } catch (IOException e)
            {
                System.out.println("Problem closing jarStreams");
                e.printStackTrace();
            }
        }

    }
}
