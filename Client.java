
import java.io.FileWriter;
import java.io.PrintWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. 
 //javac *.java
 */ //rmiregistry ()java Master 192.168.2.14()ssh a2011100308@grad2xx // area de trabalho/master-slave() java Slave 192.168.2.xx 192.168.2.minhamaquina()java Client 192.168.2.14

/**
 *
 * @author BOT
 */
public class Client {

    public static List<Long> runProgram(String host, int tamanho){
        
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            SortInterface stub = (SortInterface) registry.lookup("Master");
            //Criar vetor randomico
            List<Integer> test = new ArrayList();
            for (int i = 0; i < tamanho; i++) {
                test.add((int) (Math.random() * 1000000));
            }

            List<Integer> test2 = test;
            List<Integer> testOH = test;

            Long t0OH = System.nanoTime();
            List<Integer> responseOH = stub.overHead(testOH);
            Long tfOH = System.nanoTime();
            Long timeOH = tfOH-t0OH;
            Long t0 = System.nanoTime();
            List<Integer> response = stub.sort(test);
            Long tf = System.nanoTime();
            Long time = tf-t0;

            

            t0= System.nanoTime();
            Collections.sort(test2);
            tf = System.nanoTime();
            Long time2 = tf-t0;
			
            Long timeSemOH = time - timeOH;
			
            System.out.println("time for sort master-slave: "+time+" nanoseconds");
            //System.out.println("time for sort master-slave without overhead: "+timeSemOH+" nanoseconds");
            System.out.println("time of the overhead: "+timeOH+" nanoseconds");
            System.out.println("time for sort directly: "+time2+" nanoseconds");//milisegundos
            //caso queira imprimir a resposta(vetor ordenado)
            //for(Integer number : response){
            //    System.out.println(number);
            //}
			
            List<Long> timeList = new ArrayList<>();
			timeList.add(time);
            //timeList.add(timeSemOH);
            timeList.add(timeOH);
			timeList.add(time2);
            return timeList;
			
            } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }
    
    public static void main(String[] args) throws Exception {
	String host = (args.length < 1) ? null : args[0];
        System.setProperty("java.rmi.server.hostname", args[0]);
        
            List<String> title = new ArrayList<>();
            title.add("Size of Vector");
            title.add("Time for master-slave");
            //title.add("Time for master-slave without overhead");
            title.add("Time for overhead");
            title.add("Time for directly");
            try {
                FileWriter fw = new FileWriter("results.csv");
                PrintWriter out = new PrintWriter(fw);

                for (int i = 0; i < title.size(); i++) {
                    out.print(title.get(i) + "; ");
                }
                out.print("\n");

                int vectorSize[] = {10,100,500,1000,3000,5000,8000,10000,20000,40000,60000,80000,100000,150000,250000,400000,550000,750000,870000,1000000};
           
                for (int i = 0; i < vectorSize.length; i++) {
                    
                    List<Long> results = runProgram(host, vectorSize[i]);
                    out.print(vectorSize[i]);
					out.print(";");
                    for (int j = 0; j < results.size(); j++) {
                        out.print(results.get(j));
                        out.print(";");
                    }
                    out.print("\n");
                }
                out.flush();
                out.close();
                fw.close();

            } catch (Exception e) {
                System.err.println("Error: " + e.toString());
            }
            
        
    }
}
