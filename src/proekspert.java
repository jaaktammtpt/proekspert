import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.util.stream.*;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import java.util.Arrays;

//C:\Users\kasutaja\IdeaProjects\proekspert\out\artifacts\proekspert_jar>java -jar proekspert.jar timing.log 5

public class proekspert {
    public ArrayList<Resource> listResources = new ArrayList<Resource>();
    public ArrayList<Histo> listHisto = new ArrayList<Histo>();
    CategoryChart chart = new CategoryChartBuilder().width(800).height(600).title("Histogram").xAxisTitle("Hour").yAxisTitle("Requests").build();

    public class Histo {
        public Integer requestHour;
        public Integer requestCounter;

        public Histo(Integer requestHour, Integer requestCounter) {
            this.requestHour = requestHour;
            this.requestCounter = requestCounter;
        }

        public Integer getRequestHour() {
            return requestHour;
        }

        public void setRequestHour(Integer requestHour) {
            this.requestHour = requestHour;
        }

        public Integer getRequestCounter() {
            return requestCounter;
        }

        public void setRequestCounter(Integer requestCounter) {
            this.requestCounter = requestCounter;
        }
    }

    public class Resource {
        public String resourceName;
        public Integer requestDuration;

        public Resource(String resourceName, Integer requestDuration) {
            this.resourceName = resourceName;
            this.requestDuration = requestDuration;
        }

        public String getResourceName() {
            return resourceName;
        }

        public void setResourceName(String resourceName) {
            this.resourceName = resourceName;
        }

        public Integer getRequestDuration() {
            return requestDuration;
        }

        public void setRequestDuration(Integer requestDuration) {
            this.requestDuration = requestDuration;
        }
    }

    public void loadFile(String file, Integer topn) throws IOException{
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

        try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line;
            while((line = bufferedReader.readLine()) != null) {
                String[] resdur = line.split(" ");
                //Unique resource
                String[] resourcesplit = resdur[4].split("\\?");
                listResources.add(new Resource(resourcesplit[0], Integer.parseInt(resdur[resdur.length-1])));

                String[] timeCol = resdur[1].split(":");
                listHisto.add(new Histo(Integer.parseInt(timeCol[1]),1));
            }
        }

        Map<String, Double> map = listResources.stream()
                .collect(Collectors.groupingBy(Resource::getResourceName,Collectors.averagingInt(Resource::getRequestDuration)));

        Map<Integer, Integer> mapHist = listHisto.stream()
                .collect(Collectors.groupingBy(Histo::getRequestHour,Collectors.summingInt(Histo::getRequestCounter)));

        /*mapHist.entrySet().stream().forEach(e ->
                System.out.println("Key : " + e.getKey() + " value : " + e.getValue()));*/

        List<Integer> hourList = new ArrayList(mapHist.keySet());
        List<Integer> counterList = new ArrayList(mapHist.values());


        CategoryChart chart = new CategoryChartBuilder().width(800).height(600).title("Histogram").xAxisTitle("Hour").yAxisTitle("Requests").build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideN);
        chart.getStyler().setHasAnnotations(true);

        chart.addSeries("Hourly number of requests", hourList, counterList);

        new SwingWrapper(chart).displayChart();

        map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(topn)
                .forEach(e -> System.out.println("\t" + e.getKey() + " - " + e.getValue()));
    }




    public void printhelp(){
        String helptext = "Use following syntax to use this program: \n" +
                "java -jar proekspert.jar file n\n" +
                "\twhere: \n" +
                "\t - file is logfile name you want to use\n" +
                "\t - n is number of resources with highest average request duration. ";

        System.out.println(helptext);
    }



    public static void main(String [] args) throws Exception {

        proekspert tester = new proekspert();

        //Program start time
        long startTime = System.currentTimeMillis();

        List<String> arglist =new ArrayList<>(Arrays.asList(args));

        if (arglist.contains("-h")){
            tester.printhelp();
        }
        else {
            Integer topn = Integer.parseInt(args[1]);

            System.out.println("\n "+ "Top " + args[1] + " resources with highest average request duration:");
            tester.loadFile(args[0], topn);

            System.out.println();
        }

        //Program stop time
        long stop = System.currentTimeMillis() - startTime;
        //Print out program run time
        System.out.println("Program run time = " +stop + " ms");

    }
}
