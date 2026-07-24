import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TaxaAnalyzer {
    private String directoryA;
    private String directoryB;
    private String rank;
    private char rankLetter;
    private String validRankChoices = " Kingdom, Phylum, Class, Order, Family, Genus, or Species ";
    private HashMap<String, HashSet<String>> untrimmedSamples = new HashMap<String, HashSet<String>>(); //Map sample id to unique taxa in it's untrimmed file
    private HashMap<String, HashSet<String>> trimmedSamples = new HashMap<String, HashSet<String>>(); //Map sample id to unique taxa in it's trimmed file
    private static final int ONLY_UNTRIMMED = 0;
    private static final int ONLY_TRIMMED = 1;
    private static final int COMMON = 2;

    public TaxaAnalyzer(String directoryA, String directoryB, String rank){ 
        this.directoryA = directoryA;
        this.directoryB = directoryB;
        this.rank = rank;
    }

    public void storeTaxa(){ 
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

            String sampleID = fileName.replace("_trimmed_profile.txt", "").replace("_profile.txt", ""); //same ID for trimmed and untrimmed version 

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

            if (fileName.endsWith("_trimmed_profile.txt")) trimmedSamples.put(sampleID, uniqueTaxa);
            else untrimmedSamples.put(sampleID, uniqueTaxa);
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

    private HashMap<String, List<HashSet<String>>> getUniqueSampleMap(){
        HashMap<String, List<HashSet<String>>> uniqueSampleMap = new HashMap<String, List<HashSet<String>>>();

        for (String sampleID : untrimmedSamples.keySet()) {
            HashSet<String> untrimmed = untrimmedSamples.get(sampleID); 
            HashSet<String> trimmed = trimmedSamples.get(sampleID);
            
            if (trimmed == null) continue; //if matching sample doesn't exist in second directory, skip. This will result in a null uniqueSets at this sampleID though so we'll catch that in the print method.

            HashSet<String> onlyInUntrimmed = new HashSet<String>(untrimmed); 
            HashSet<String> onlyInTrimmed = new HashSet<String>(trimmed); 
            HashSet<String> inBoth = new HashSet<String>(untrimmed); 
            
            //HashSets allow simple set operations
            onlyInUntrimmed.removeAll(trimmed); // untrimmed \ trimmed 
            onlyInTrimmed.removeAll(untrimmed); // trimmed \ untrimmed
            inBoth.retainAll(trimmed); // untrimmed ∩ trimmed
            
            List<HashSet<String>> uniqueSets = List.of(onlyInUntrimmed,onlyInTrimmed,inBoth); //Yes we are hardcoding the indices for now
            uniqueSampleMap.put(sampleID, uniqueSets);
        }
        return uniqueSampleMap;
    }

    private void printSampleData(){ 
        HashMap<String, List<HashSet<String>>> uniqueSampleMap = getUniqueSampleMap();

        for (String sampleID : untrimmedSamples.keySet()) { //Not neccesarily same size as trimmed set, safer to loop over untrimmed
            List<HashSet<String>> uniqueSets = uniqueSampleMap.get(sampleID);

            if (uniqueSets == null) { //if there wasn't a matching trimmed sample, we continued in getUniqueSampleMap(), so there isn't a uniqueSets list.
                System.out.println("Sample: " + sampleID + " has no trimmed counterpart");
                continue;
            }

            HashSet<String> onlyInUntrimmedTaxa = uniqueSets.get(ONLY_UNTRIMMED);
            HashSet<String> onlyInTrimmedTaxa = uniqueSets.get(ONLY_TRIMMED);
            HashSet<String> commonTaxa = uniqueSets.get(COMMON);

            System.out.println("\nSample: " + sampleID);
            System.out.println("Only in Untrimmed " + rank + ": " + String.join(", ", onlyInUntrimmedTaxa) + "     Count: " + onlyInUntrimmedTaxa.size());
            System.out.println("Only in Trimmed " + rank + ": " + String.join(", ", onlyInTrimmedTaxa) + "     Count: " + onlyInTrimmedTaxa.size());
            System.out.println("Common " + rank + ": " + String.join(", ", commonTaxa) + "     Count: " + commonTaxa.size());
        }
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
        String fileDirectory = args[0]; //For now not going to use command line                                                                                                                 
        String tempDirectory = args[1];
        String taxonRank = args[2];
        
        TaxaAnalyzer taxaAnalyzer = new TaxaAnalyzer(fileDirectory, tempDirectory, taxonRank);
        taxaAnalyzer.storeTaxa();
        taxaAnalyzer.printSampleData();
    }
}
