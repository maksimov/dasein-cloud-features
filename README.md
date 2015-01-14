# dasein-cloud-features
Dasein Cloud Feature Matrix
===========================

Allows you to dynamically build the service implementation matrix for arbitrary Dasein drivers. The driver selection is 
per the `pom.xml`, by default the seven major cloud drivers are configured. 

Run with `mvn exec:java`.

The output is an ASCII table, 
which looks like this (actual sample taken in Jan, 2015):

```
|                           |AWS      |Citrix   |Google   |Joyent   |Microsoft|OpenStack|VMware   |
|---------------------------|---------|---------|---------|---------|---------|---------|---------|
|admin                      |X        |         |         |         |         |         |         |
|   account                 |         |         |         |         |         |         |         |
|   billing                 |         |         |         |         |         |         |         |
|   prepayment              |X        |         |         |         |         |         |         |
|---------------------------|---------|---------|---------|---------|---------|---------|---------|
|ci                         |         |         |         |         |         |         |         |
|---------------------------|---------|---------|---------|---------|---------|---------|---------|
|compute                    |X        |X        |X        |X        |X        |X        |X        |
|   affinityGroup           |         |         |         |         |X        |         |X        |
|   autoScaling             |X        |         |         |         |         |         |         |
|   image                   |X        |X        |X        |X        |X        |X        |X        |
|   snapshot                |X        |X        |X        |         |         |X        |         |
|   virtualMachine          |X        |X        |X        |X        |X        |X        |X        |
|   volume                  |X        |X        |X        |         |X        |X        |X        |
|---------------------------|---------|---------|---------|---------|---------|---------|---------|
|network                    |X        |X        |X        |         |X        |X        |X        |
|   dns                     |X        |         |         |         |         |         |         |
|   firewall                |X        |X        |X        |         |         |X        |         |
|   ipAddress               |X        |X        |X        |         |         |X        |         |
|   loadBalancer            |X        |X        |X        |         |X        |         |         |
|   networkFirewall         |X        |         |         |         |         |         |         |
|   vlan                    |X        |X        |X        |         |X        |X        |X        |
|   vpn                     |         |         |         |         |         |         |         |
|---------------------------|---------|---------|---------|---------|---------|---------|---------|
|platform                   |X        |         |X        |         |         |         |         |
|   cDN                     |X        |         |         |         |         |         |         |
|   dataWarehouse           |         |         |         |         |         |         |         |
|   keyValueDatabase        |X        |         |         |         |         |         |         |
|   messageQueue            |         |         |         |         |         |         |         |
|   monitoring              |X        |         |         |         |         |         |         |
|   pushNotification        |X        |         |         |         |         |         |         |
|   relationalDatabase      |X        |         |X        |         |         |         |         |
|---------------------------|---------|---------|---------|---------|---------|---------|---------|
|storage                    |X        |         |X        |X        |X        |         |         |
|   blobStore               |X        |         |X        |         |X        |         |         |
|   offlineStorage          |X        |         |         |         |         |         |         |
|   onlineStorage           |X        |         |X        |         |X        |         |         |
```