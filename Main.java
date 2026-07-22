public class Main {
    
    public static void main(String[] args) {
        String fileDirectory = args[0]; //test directory, pass whichever needs to be analyzed  "/Users/knaeimi/Documents/GitHub/MetaPhlAn/metaphlan" for command line
        String taxonRank = args[1]; //pass Kingdom, Phylum, Class, Order, Family, Genus, or Species (case insensitive)
        
        TaxaAnalyzer taxaAnalyzer = new TaxaAnalyzer(fileDirectory, taxonRank);
        taxaAnalyzer.countTaxa();
    }
}
