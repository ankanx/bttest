package sample;

import javax.bluetooth.*;
import java.io.IOException;
import java.util.Vector;



/**
 * Minimal Device Discovery example.
 */
public class RemoteDeviceDiscovery {

    public static final Vector/*<RemoteDevice>*/ devicesDiscovered = new Vector();

    public static void main(String[] args) throws IOException, InterruptedException {

        initialize();

        final Object inquiryCompletedEvent = new Object();

        devicesDiscovered.clear();

        DiscoveryListener listener = new DiscoveryListener() {

            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                System.out.println("Device " + btDevice.getBluetoothAddress() + " found");
                devicesDiscovered.addElement(btDevice);
                try {
                    System.out.println("     name " + btDevice.getFriendlyName(false));
                } catch (IOException cantGetDeviceName) {
                }
            }

            public void inquiryCompleted(int discType) {
                System.out.println("Device Inquiry completed!");
                synchronized(inquiryCompletedEvent){
                    inquiryCompletedEvent.notifyAll();
                }
            }

            public void serviceSearchCompleted(int transID, int respCode) {
            }

            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
            }
        };

        synchronized(inquiryCompletedEvent) {
            boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
            if (started) {
                System.out.println("wait for device inquiry to complete...");
                inquiryCompletedEvent.wait();
                System.out.println(devicesDiscovered.size() +  " device(s) found");
            }
        }
    }

    public static LocalDevice initialize() {
        System.out.println("Initializing local Bluetooth device.....");
        LocalDevice local = null;
        try {
            local = LocalDevice.getLocalDevice();
            System.out.println("Local Address: " + local.getBluetoothAddress());
            System.out.println("Local Name: " + local.getFriendlyName());
        } catch (BluetoothStateException e) {
            System.out.println("Bluetooth Adapter not started.");
        }
        System.out.println("Bluetooth device started!");
        return local;

    }

}