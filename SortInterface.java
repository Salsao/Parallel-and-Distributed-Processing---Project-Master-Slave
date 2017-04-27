
import java.rmi.Remote;
import java.rmi.RemoteException;
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
public interface SortInterface extends Remote{
    public List<Integer> sort(List<Integer> list) throws RemoteException;
    public List<Integer> overHead(List<Integer> list) throws RemoteException;
}


