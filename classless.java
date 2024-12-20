import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class classless {

    public static String intToIp(int ip) {
        return ((ip >> 24) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + (ip & 0xFF);
    }

    public static int calculateTotalAddressSpace(int prefix) {
        return (int) Math.pow(2, (32 - prefix));
    }

    public static void calculateSubnetRange(String ip, int prefix, int numberOfSubnets, int[] addressBlocks) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            byte[] ipBytes = inetAddress.getAddress();
            int ipInt = 0;
            for (byte b : ipBytes) {
                ipInt = (ipInt << 8) | (b & 0xFF);
            }

            int totalAddressSpace = calculateTotalAddressSpace(prefix);
            System.out.println("Total Address Space for prefix /" + prefix + ": " + totalAddressSpace);

            Integer[] indices = new Integer[numberOfSubnets];
            for (int i = 0; i < numberOfSubnets; i++) {
                indices[i] = i;
            }

            Arrays.sort(indices, Comparator.comparingInt(i -> -addressBlocks[i]));

            int totalUsedAddressSpace = 0;
            int totalDepletion = 0;

            int currentNetworkAddress = ipInt;
            for (int index : indices) {
                int block = addressBlocks[index];

                int newPrefix = 32 - (int) (Math.ceil(Math.log(block) / Math.log(2))); 
                int subnetAddressSpace = calculateTotalAddressSpace(newPrefix);
                
                int subnetMask = ~((1 << (32 - newPrefix)) - 1);
                int networkAddress = currentNetworkAddress & subnetMask;
                int broadcastAddress = networkAddress | ~subnetMask;

             
                int depletion = Math.max(0, subnetAddressSpace - block); 
                totalDepletion += depletion; 
                totalUsedAddressSpace += block;

                System.out.println("Subnet " + (index + 1) + ":");
                System.out.println("  Network Address: " + intToIp(networkAddress) + "/" + newPrefix);
                System.out.println("  Address Range: " + intToIp(networkAddress) + " - " + intToIp(broadcastAddress));
                System.out.println("  Address Depletion Area: " + depletion + " addresses");

                currentNetworkAddress = broadcastAddress + 1;
            }

            int finalIpRange = totalUsedAddressSpace + totalDepletion; 

            int overallDepletion = totalAddressSpace - finalIpRange; 
             
            int  overalladdressDepletionArea = totalAddressSpace  -   finalIpRange + totalDepletion;
            System.out.println("Overall Address Depletion Area: " + totalAddressSpace + " - " + finalIpRange + " + " + totalDepletion +" = " +overalladdressDepletionArea+ "   addresses");

        } catch (UnknownHostException e) {
            System.out.println("Invalid IP address format");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter IP address  ");
        String ip = scanner.nextLine();
        System.out.print("Enter original prefix length  ");
        int prefix = scanner.nextInt();

        System.out.print("Enter the number of subnets: ");
        int numberOfSubnets = scanner.nextInt();
        
        int[] addressBlocks = new int[numberOfSubnets];
        for (int i = 0; i < numberOfSubnets; i++) {
            System.out.print("Enter address block size for subnet " + (i + 1) + ": ");
            addressBlocks[i] = scanner.nextInt();
        }

        calculateSubnetRange(ip, prefix, numberOfSubnets, addressBlocks);
    }
}