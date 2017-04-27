
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author BOT
 */
public class Slave extends UnicastRemoteObject implements SortInterface{
    private static Slave slave;
    public Slave() throws RemoteException {
        super();
    }

    @Override
    public List<Integer> sort(List<Integer> list) throws RemoteException {
        try{
            Collections.sort(list);         
        }catch(Exception e){
            System.out.println(e);
            try{
                Registry registry = LocateRegistry.getRegistry();
                MasterRegistry stub = (MasterRegistry) registry.lookup("Master");
                stub.unbind(slave);
                
            }catch (Exception ex){
                System.out.println(ex);
            }  
        }
        return list;
    }
    
    public static void main(String args[]) {
        System.setProperty("java.rmi.server.hostname", args[0]);
        String host = (args.length < 2) ? null : args[1];
        try{
            Registry registry = LocateRegistry.getRegistry(host);
            MasterRegistry stub = (MasterRegistry) registry.lookup("Master");
            slave = new Slave();
            stub.bind(slave);
            System.out.println("Slave running");
        }catch(Exception e){
            System.out.println(e);
        }
        
    }

    @Override
    public List<Integer> overHead(List<Integer> list) throws RemoteException {
        return list;
    }
}
