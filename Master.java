
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author BOT
 */
public class Master implements SortInterface, MasterRegistry {

    private List<SortInterface> slaves;

	//funcao para o mestre ordenar as 2 listas que recebeu dos escravos
    public static void merge(List<Integer> A, List<Integer> B, List<Integer> C) {
        int i, j, k, m, n;
        i = 0;
        j = 0;
        k = 0;
        m = A.size();
        n = B.size();
        while (i < m && j < n) {
            if (A.get(i) <= B.get(j)) {
                C.add(k, A.get(i));
                i++;
            } else {
                C.add(k, B.get(j));
                j++;
            }
            k++;
        }
        if (i < m) {
            for (int p = i; p < m; p++) {
                C.add(k, A.get(p));
                k++;
            }
        } else {
            for (int p = j; p < n; p++) {
                C.add(k, B.get(p));
                k++;
            }
        }
    }

    public Master() throws RemoteException {
        super();
        this.slaves = new ArrayList();
    }

    @Override
    public List<Integer> sort(List<Integer> list) throws RemoteException {
        try {

            //Criar uma lista de threads
            List<RunningSlave> slaveThreads = new ArrayList<RunningSlave>();
            List<Thread> threads = new ArrayList<Thread>();
            RunningSlave runningSlave;
            Thread thread;

            //Criar subvetores para os escravos, e logo apos criar, o escravo ja ordenar
            int ind = 0;
            for (int start = 0; start < list.size(); start += (list.size() / slaves.size())) {
                int end = Math.min(start + (list.size() / slaves.size()), list.size());
                List<Integer> sublist = new ArrayList<Integer>(list.subList(start, end));
                //caso a divisao nao seja exata
                if ((list.size() - start - (list.size() / slaves.size())) <= slaves.size() && (list.size() - start - (list.size() / slaves.size())) != 0) {
                    int sobra = list.size() - start - (list.size() / slaves.size());
                    for (int i = 0; i < sobra; i++) {
                        sublist.add(list.get(i + start + (list.size() / slaves.size())));
                    }
                    try {
                        //entrega para o escravo ordenar
                        runningSlave = new RunningSlave(slaves.get(ind), sublist);
                        slaveThreads.add(runningSlave);
                        thread = new Thread(runningSlave);
                        threads.add(thread);
                        thread.start();
                    } catch (Exception e) {
                        System.out.println("Erro ao entregar a tarefa ao escravo " + ind);
                    }
                    break;
                }
                try {
                    //entrega para o escravo ordenar
                    runningSlave = new RunningSlave(slaves.get(ind), sublist);
                    slaveThreads.add(runningSlave);
                    thread = new Thread(runningSlave);
                    threads.add(thread);
                    thread.start();
                } catch (Exception e) {
                    System.out.println("Erro ao entregar a tarefa ao escravo " + ind);
                }
                ind++;
            }
            //adicionar threads
            for (Thread threadOne : threads) {
                try {
                    threadOne.join();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }

            //adiciona as sublistas
            List<List<Integer>> subLists = new ArrayList<>();
            for (RunningSlave i : slaveThreads) {
                subLists.add(i.getsubList());
            }

            //vai ordenando os resultados
            while (subLists.size() > 2) {
                List<Integer> a = subLists.remove(0);
                List<Integer> b = subLists.remove(0);
                List<Integer> subMerge = new ArrayList<Integer>();
                merge(a, b, subMerge);
                subLists.add(subMerge);
            }

            //adiciona e ordena as ultimas 2 listas
            List<Integer> sortedLists = new ArrayList<Integer>();
            if (subLists.size() == 2) {
                List<Integer> a = subLists.remove(0);
                List<Integer> b = subLists.remove(0);
                merge(a, b, sortedLists);
            } else {
                sortedLists = subLists.remove(0);
            }

            return sortedLists;

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;

    }

    @Override
    public void bind(Remote remote) throws RemoteException {
        try {
            this.slaves.add((SortInterface) remote);
            System.out.println("Slave added");
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

    }

    public static void main(String args[]) {
        try {
	    System.setProperty("java.rmi.server.hostname", args[0]);
	    Master obj = new Master();
	    SortInterface objref = (SortInterface) UnicastRemoteObject.exportObject(obj,2001);
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind("Master", objref);
            System.out.println("Master running");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void unbind(Remote remote) throws RemoteException {
        try {
            this.slaves.remove((SortInterface) remote);
            System.out.println("Slave removed");
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

	//para calcular o overhead
    @Override
    public List<Integer> overHead(List<Integer> list) throws RemoteException {

	try {

            //Criar uma lista de threads
            List<RunningSlaveOH> slaveThreads = new ArrayList<RunningSlaveOH>();
            List<Thread> threads = new ArrayList<Thread>();
            RunningSlaveOH runningSlave;
            Thread thread;

            List<Integer> sublist = new ArrayList<Integer>(list.subList(0, list.size()/slaves.size()));

            for(int i=0;i<slaves.size();i++){
                runningSlave = new RunningSlaveOH(slaves.get(i), sublist);
                slaveThreads.add(runningSlave);
                thread = new Thread(runningSlave);
                threads.add(thread);
                thread.start();
            }

            for (Thread threadOne : threads) {
                try {
                    threadOne.join();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }

            return list;

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;

    }

    public class RunningSlave implements Runnable {

        private SortInterface slave;
        private List<Integer> subList;

        public RunningSlave(SortInterface slave, List<Integer> numbers) throws RemoteException {
            this.slave = slave;
            this.subList = numbers;
        }

        public List<Integer> getsubList() {
            return subList;
        }

        @Override
        public void run() {
            try {
                subList = slave.sort(subList);
            } catch (Exception ex) {
                Logger.getLogger(Master.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public class RunningSlaveOH implements Runnable {

        private SortInterface slave;
        private List<Integer> subList;

        public RunningSlaveOH(SortInterface slave, List<Integer> numbers) throws RemoteException {
            this.slave = slave;
            this.subList = numbers;
        }

        public List<Integer> getsubList() {
            return subList;
        }

        @Override
        public void run() {
            try {
                subList = slave.overHead(subList);
            } catch (Exception ex) {
                Logger.getLogger(Master.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
