public class Main {
    
    public static void main(String[] args) {
        String fileDirectory = "/Users/knaeimi/Documents/GitHub/MetaPhlAn/metaphlan"; //test directory, pass whichever needs to be analyzed
        String taxonRank = "Kingdom"; //pass Kingdom, Phylum, Class, Order, Family, Genus, or Species (case insensitive)
        TaxaAnalyzer taxaAnalyzer = new TaxaAnalyzer(fileDirectory, taxonRank);
        taxaAnalyzer.countTaxa();
    }
}
