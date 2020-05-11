import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.jar.JarInputStream;

public class JarInputHandler {

    String[] args;
    List<String> argsList = new ArrayList<>();
    public JarInputHandler(String[] args){
        this.args = args;
        this.argsList = Arrays.asList(args);
    }
    public void run(){
        JarDisplayer jarDisplayer = null;
        try{
            if(argsList.contains("--i")){
                String jarPath = args[argsList.indexOf("--i") + 1];
                jarDisplayer = new JarDisplayer(jarPath);
                if(argsList.contains("--list-packages")){
                    jarDisplayer.listPackages();
                }
                if(argsList.contains("--list-classes")){
                    jarDisplayer.listClasses();
                }
                if(argsList.contains("--list-methods")){
                    String classPath = args[argsList.indexOf("--list-methods") + 1];
                    jarDisplayer.listMethods(classPath);
                }
                if(argsList.contains("--list-ctors")){
                    String classPath = args[argsList.indexOf("--list-ctors") + 1];
                    jarDisplayer.listConstructors(classPath);
                }
                if(argsList.contains("--list-fields")){
                    String classPath = args[argsList.indexOf("--list-fields") + 1];
                    jarDisplayer.listFields(classPath);
                }

                if(argsList.contains("--o")){
                    String outputFileName = args[argsList.indexOf("--o") + 1];
                    if(argsList.contains("--script")){
                        String scriptFile = args[argsList.indexOf("--script") + 1];
                        ArrayList<String> lineArray = new ArrayList<>();
                        JarScriptExpolorer jarScriptExpolorer = new JarScriptExpolorer(jarDisplayer);
                        File file = new File(scriptFile);
                        try{
                            Scanner scanner = new Scanner(file);
                            while(scanner.hasNextLine()) {
                                lineArray = new ArrayList<>(Arrays.asList(scanner.nextLine().split(" ")));
                                jarScriptExpolorer.execute(lineArray);
                            }
                            JarInputStream jarInputStream = new JarInputStream(new FileInputStream(args[argsList.indexOf("--i") + 1]));
                            JarPacker jarPacker = new JarPacker(jarScriptExpolorer.getAddedClasses(),jarScriptExpolorer.getAddedPackages()
                                    ,jarDisplayer.getJarFile(), outputFileName, jarInputStream,jarDisplayer.getClassPool());
                            jarPacker.pack();
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("nieprawidlowa ilosc argumentow");
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(jarDisplayer != null){
                jarDisplayer.clean();
            }
        }
    }
}
