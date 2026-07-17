public class Main {
    
    public static void main(String[] args) {
        String fileDirectory = "/Users/knaeimi/Documents/GitHub/MetaPhlAn/metaphlan"; //test directory, pass whichever needs to be analyzed
        TaxaAnalyzer taxaAnalyzer = new TaxaAnalyzer(fileDirectory);
        taxaAnalyzer.countTaxa();
    }
}
