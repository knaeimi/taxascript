import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TaxaAnalyzer {
    private String directory;
    private String rank;
    private char rankLetter;
    private String validRankChoices = " Kingdom, Phylum, Class, Order, Family, Genus, or Species ";

    public TaxaAnalyzer(String directory, String rank){
        this.directory = directory;
        this.rank = rank;
        rankLetter = determineRank();
    }

    /**
     * Prints out taxa number for each profile in the passed directory
     */
    public void countTaxa(){ //Takes in rank (Kingdom, Phylum, Class, Order, Family, Genus, or Species)
        Path directoryPath = Paths.get(directory); //convert directory string to a concrete file path
        File[] files = directoryPath.toFile().listFiles(); 

        for (File file : files) {

            if (rankLetter == 's') System.out.println("Testing");

            int taxaCount = 0; //each file in the directory has its own taxa count, so reset after processing each one

            if (!file.getName().endsWith("_profile.txt")) continue; //skip files in the directory that aren't metaphlan taxonomic profiles

            System.out.println("Computing " + rank + " count of " + file.getName()); //Valid profile, so lets print which one it is for debugging

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {//sytax here: Pass rescource in try and it will close the reader for you at the end. Syntactic sugar.
                
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#")) { //each non comment line is technically new taxa.
                        continue; //So we'll skip comments
                    }
                    if (validTaxon()) taxaCount++;
                }
                System.out.println(file.getName() + " has a " + rank + " count of "+ taxaCount);
            } 
            
            catch (IOException e) { //try-catch syntax for debugging
                e.printStackTrace();
            }
        }
    }

    private char determineRank(){
        rank = rank.strip().toLowerCase();
        rankLetter = rank.charAt(0);

        if (validRank(rankLetter)) return rankLetter;
        else throw new IllegalArgumentException(rank + " is not a valid rank! Ranks: " + validRankChoices);
    }

    private boolean validRank(char rankLetter){
        if (rankLetter == 'k' || rankLetter == 'p' || rankLetter == 'c' || rankLetter == 'o' || 
        rankLetter == 'f' || rankLetter == 'g' || rankLetter == 's') return true;
        return false;
    }

    private boolean validTaxon(){ //main taxa parsing algorithm


        
        return true; //temp
    }
}
