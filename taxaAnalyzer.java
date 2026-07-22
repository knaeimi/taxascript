import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

public class TaxaAnalyzer {
    private String directory;
    private String rank;
    private char rankLetter;
    private String validRankChoices = " Kingdom, Phylum, Class, Order, Family, Genus, or Species ";
    private HashSet<String> uniqueTaxa = new HashSet<String>(); //HashSet because we care about unique taxa

    public TaxaAnalyzer(String directory, String rank){
        this.directory = directory;
        this.rank = rank;
    }

    /**
     * Prints out taxa number for each profile in the passed directory
     */
    public void countTaxa(){ //Takes in rank (Kingdom, Phylum, Class, Order, Family, Genus, or Species)
        Path directoryPath = Paths.get(directory); //convert directory string to a concrete file path
        File[] files = directoryPath.toFile().listFiles(); 

        for (File file : files) {

            if (!file.getName().endsWith("_profile.txt")) continue; //skip files in the directory that aren't metaphlan taxonomic profiles

            System.out.println("Computing " + rank + " count of " + file.getName()); //Valid profile, so lets print which one it is for debugging

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {//sytax here: Pass rescource in try and it will close the reader for you at the end. Syntactic sugar.
                
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#")) { //each non comment line is technically new taxa.
                        continue; //So we'll skip comments
                    }
                    addUniqueTaxa(line);
                }
                System.out.println(file.getName() + " has a " + rank + " count of "+ uniqueTaxa.size() + ", and those unique " + rank + " are: " + String.join(", ", uniqueTaxa));
                uniqueTaxa.clear(); //each file needs a new set of taxa
            } 
            
            catch (IOException e) { 
                e.printStackTrace();
            }
        }
    }

    private char getRankLetter(){
        rank = rank.strip().toLowerCase();
        rankLetter = rank.charAt(0);

        if (validRank()) return rankLetter;
        else throw new IllegalArgumentException(rank + " is not a valid rank! Ranks: " + validRankChoices);
    }

    private void addUniqueTaxa(String line){ //main taxa parsing algorithm
       String clade = line.split("\t")[0]; //tabs seperate clades.. if we dont split by tab then we double count ("k_Bacteria" != " K_Bacteria")
       String ranks[] = clade.split("\\|"); //two backslashes because we need to escape java and regex layers 
        
       for (String rank : ranks){
        if (rank.charAt(0) == getRankLetter()) {
            uniqueTaxa.add(rank); //guarenteed uniqueness so we'll only add this specific taxa once
            break; //we found our chosen taxon rank on this line, no need to keep iterating through it
        }
       }
    }

    private boolean validRank(){
        return (isKingdom()|| isPhylum() || isClass() || isOrder() || isFamily() || isGenus() || isSpecies());
    }

    private boolean isKingdom(){
        if (rankLetter == 'k'){
            rank = "Kingdom";
            return true;
        }
        return false;
    }

    private boolean isPhylum(){
        if (rankLetter == 'p'){
            rank = "Phylum";
            return true;
        }
        return false;
    }

    private boolean isClass(){
        if (rankLetter == 'c'){
            rank = "Class";
            return true;
        }
        return false;
    }

    private boolean isOrder(){
        if (rankLetter == 'o'){
            rank = "Order";
            return true;
        }
        return false;
    }

    private boolean isFamily(){
        if (rankLetter == 'f'){
            rank = "Family";
            return true;
        }
        return false;
    }

    private boolean isGenus(){
        if (rankLetter == 'g'){
            rank = "Genus";
            return true;
        }
        return false;
    }

    private boolean isSpecies(){
        if (rankLetter == 's'){
            rank = "Species";
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        String fileDirectory = args[0]; //test directory, pass whichever needs to be analyzed  "/Users/knaeimi/Documents/GitHub/MetaPhlAn/metaphlan" for command line
        String taxonRank = args[1]; //pass Kingdom, Phylum, Class, Order, Family, Genus, or Species (case insensitive)
        
        TaxaAnalyzer taxaAnalyzer = new TaxaAnalyzer(fileDirectory, taxonRank);
        taxaAnalyzer.countTaxa();
    }
}
