
import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author BOT
 */
public interface MasterRegistry extends Remote{
    public void bind(Remote remote) throws RemoteException;
    public void unbind(Remote remote) throws RemoteException;
}
