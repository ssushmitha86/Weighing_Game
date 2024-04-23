# Weighing_Game_Test_Challenge
This repository contains a Weighing Game take-home coding challenge.

## Prerequisites
- [Java 1.8](https://www.java.com/download/ie_manual.jsp)
* Junit 
+ Selenium

## Installation
1.Clone this repository to your local machine and import in any IDE(I have used IntelliJ)  <br><br>
2.Navigate to the project directory `Weighing_Game` <br><br>
3.Build the project using Maven  <br><br>
If this error `java: package org.junit does not exist` is encountered during build,follow the step below picture  <br><br>
![image](https://github.com/ssushmitha86/Balance-scale-Game-Test/assets/115060706/6741d5b0-8d25-4d5e-aafa-fc8ff16f8aa0)
 <br><br>
4.Download Chromedriver compatible with your chrome browser  <br><br>
5.Add Chromedriver path manually in the java file as specified below  <br><br>
`Weighing_Game\test\Weighing_Game.java`, in line 28 add your 'chromedriver.exe' path  <br><br>
6.Switch to test view and Run all tests

## Brief description of the testcases used:

1.Functional Tests:<br><br>
    a) Positive test case for correct gold bar pick <br>
    b) Checking the entries of Weighings list <br>
    c)Negative test case for wrong gold bar pick <br><br>

2.Test for Empty Bowls with no bars in it <br><br>
3.Test for Single gold bar with same number on both left and right scales <br><br>
4.Test for Multiple gold bars with same number on both left and right scales <br><br>
5.Test for Multiple gold bar with same number (duplicates) on left side only <br><br>
6.Test for Multiple gold bar with same number (duplicates) on right side only <br><br>
7.Test for Empty bowl with no bar on one side error check <br><br>
8.Test for Invalid charater entry in left/right bowls <br><br>
9.Test to check working of the reset button <br><br>
    
Sample screenshot of passed test cases

![image](https://github.com/ssushmitha86/Balance-scale-Game-Test/assets/115060706/85379507-4716-4b69-80ed-8a916c692798)
