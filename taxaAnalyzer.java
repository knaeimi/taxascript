import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TaxaAnalyzer {

    private String directory;

    //Take in a file directory of taxa profiles, 

    public TaxaAnalyzer(String directory){
        this.directory = directory;
    }

    /**
     * 
     * @return # of taxa found in each metaphlan text file in the passed directory
     */
    public void countTaxa(){
        Path directoryPath = Paths.get(directory); //convert directory string to a concrete file path
        File[] files = directoryPath.toFile().listFiles(); 

        for (File file : files) {

            int taxaCount = 0; //each file in the directory has its own taxa count, so reset after processing each one

            if (!file.getName().endsWith("_profile.txt")) continue; //skip files in the directory that aren't metaphlan taxonomic profiles

            System.out.println("Computing taxa count of " + file.getName()); //Valid profile, so lets print which one it is for debugging

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {//sytax here: Pass rescource in try and it will close the reader for you at the end. Syntactic sugar.
                
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#")) {
                        continue;
                    }

                    taxaCount++;
                }
                System.out.println(file.getName() + " has a taxa count of: " + taxaCount);
            } 
            
            catch (IOException e) { //try-catch syntax for debugging
                e.printStackTrace();
            }
        }
    }


}
