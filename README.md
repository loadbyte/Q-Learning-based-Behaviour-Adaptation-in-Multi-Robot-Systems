# Garbage-Collector
  
Autonomous garbage collection and garbage segregation, where the environment has different garbages scattered around, the robot should move around this environment and pick this objects(garbage) and drop it into appropriate bins, while keeping energy in check, if energy is getting lower than threshold, the robot should go to charing station and recharge its energy.

More information about the assignment can be found in the report.  
* Garbage_Collector_Project_Report.pdf

A Demo of the project can be found in this link.  
* https://youtu.be/rCjFV8R2Cls

## Compile and execute:
### To run in bluetooth: (Connection from Lego NXT to Computer / Raspberry Pi)
1) Change ./QGarbagePC/GarbageAgentPC.java line number: 31,  
   replace Consts.USB with Consts.BT
2) Change ./QGarbage/GarbageAgentNXT.java line number: 49,  
   replace Button.ID_RIGHT with Button.ID_LEFTasdfsadfaf
3) Change ./QGarbage/makefile line number: 11,  
   replace -u with -b.

### To run in USB: (Connection from Lego NXT to Computer / Raspberry Pi)
   Default code is written for USB. No change is required.

### To compile the program,
* Execute Lego NXJ program first.  
  * Goto QGarbage folder  
    * cd QGarbage  
  * Position the robot towards any side of the arena to calibrate the directions.
  * Execute makefile or execute the commands inside the makefile.  
    * make  
* Execute Lego NXT PC program  
  * Goto QGarbagePC folder
    * cd QGarbagePC
  * Execute the makefile or execute the commands inside the makefile.
    * make
* To run Multi Robot system.
  * Copy the same code to all the computers.
  * Edit the ./QGarbagePC/Consts.java file
    * Line number: 23
    * Give unique numbers to each system in the range (1-254)
  * All the computers should be connected to the same network.
  * I would recommend to test with a dedicated router.
    * Since, we use multicast broadcast service, the connection is not reliable for communication, if it is already managing some traffic.
