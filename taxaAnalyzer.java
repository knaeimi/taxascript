import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;

public class TaxaAnalyzer {
    private String directoryA;
    private String directoryB;
    private String rank;
    private char rankLetter;
    private String validRankChoices = " Kingdom, Phylum, Class, Order, Family, Genus, or Species ";
    private HashMap<String, HashSet<String>> sampleTaxa = new HashMap<String, HashSet<String>>(); //Maps each MetaPhlAn filename to the set of uniqueTaxa in that sample

    public TaxaAnalyzer(String directoryA, String directoryB, String rank){ //Next steps: Take in directoryA and directoryB. Map each untrimmed to its trimmed counterpart, find intersection.
        this.directoryA = directoryA;
        this.directoryB = directoryB;
        this.rank = rank;
    }

    /**
     * For debugging.
     */
    public void printSampleTaxa(){
        for (String sample : sampleTaxa.keySet()) {
            HashSet<String> uniqueTaxa = sampleTaxa.get(sample);
            System.out.println("Sample: " + sample + "     Unique " + rank + ": " + String.join(",", uniqueTaxa) + "     Count: " + uniqueTaxa.size());
        }
    }

    public void storeTaxa(){ //Will run for directoryA and directoryB.
        String [] directories = {directoryA, directoryB};

        for (String directory : directories){
            Path directoryPath = Paths.get(directory); 
            File[] files = directoryPath.toFile().listFiles(); 
            processTaxa(files);
        }
    }

    private void processTaxa(File[] directoryFiles){
        for (File file : directoryFiles) {
            String fileName = file.getName();

            if (!fileName.endsWith("_profile.txt")) continue; //skip files in the directory that aren't metaphlan taxonomic profiles

            HashSet<String> uniqueTaxa = new HashSet<String>(); //create hashset after fileName check so we don't create useless hashsets for random files

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#")) { //each non comment line is technically new taxa.
                        continue; //So we'll skip comments
                    }
                    addUniqueTaxa(line, uniqueTaxa);
                }
            } 
            
            catch (IOException e) { 
                e.printStackTrace();
            }
            sampleTaxa.put(fileName,uniqueTaxa); //outside try-catch so an IOException doesn't affect us being able to store results
        }
    }

    private char getRankLetter(){
        rank = rank.strip().toLowerCase();
        rankLetter = rank.charAt(0);

        if (validRank()) return rankLetter;
        else throw new IllegalArgumentException(rank + " is not a valid rank! Ranks: " + validRankChoices);
    }

    private void addUniqueTaxa(String line, HashSet<String> uniqueTaxa){ //taxa parsing algorithm
       String clade = line.split("\t")[0]; 
       String ranks[] = clade.split("\\|"); 
        
       for (String rank : ranks){
        if (rank.charAt(0) == getRankLetter()) {
            uniqueTaxa.add(rank); 
            break; //we found our chosen taxon rank on this line so we back off
        }
       }
    }

    private boolean validRank(){
        return (isKingdom()|| isPhylum() || isClass() || isOrder() || isFamily() || isGenus() || isSpecies());
    }

    private void findIntersection(){
        //Find common taxa between directoryA and directoryB
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
        String fileDirectory = "/Users/knaeimi/Documents/GitHub/MetaPhlAn/metaphlan"; //For now am not going to use command line input
        String tempDirectory = "";
        String taxonRank = "Phylum";
        
        TaxaAnalyzer taxaAnalyzer = new TaxaAnalyzer(fileDirectory, tempDirectory, taxonRank);
        taxaAnalyzer.storeTaxa();
        taxaAnalyzer.printSampleTaxa();
    }
}
