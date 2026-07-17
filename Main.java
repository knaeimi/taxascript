public class Main {
    
    public static void main(String[] args) {
        String fileDirectory = "/Users/knaeimi/Documents/GitHub/MetaPhlAn/metaphlan";
        TaxaAnalyzer taxaAnalyzer = new TaxaAnalyzer(fileDirectory);
        taxaAnalyzer.countTaxa();
    }
}
