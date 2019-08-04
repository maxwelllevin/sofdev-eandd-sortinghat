#####SORTING HAT#####
Authored by: James Tostado, Lars Mayrand, Mack Beveridge, Maxwell Levin, Nick Tan, Sam Peers Nitzberg

                         oooo     oooo
                      o$$"""$oo$$"""""$o
                     $"      $$"      $$$$
                    $"      ""        $$$$$o
                    $                 $$$$$o
                   $                   $$$$$$
                  $"     $$     $$$$$   "$$$$$
                  $      $$     $$       $$$$$$
                 $"      $$     $$       $$$$$$
                 $       $$     $$        $$$$$
                 $       $$$$$  $$$$$    o$$$$$
                 $                       $$$$$$
                 $                      o$$$$$$
              ooo                        o$$$$$$$
      ooo$$$$"" $                   oo$$$$$""""""oooo
   oo"$$$$$$$ oo"" oooooooooooooooo$$"""           o$$"oo
  o"  $$$$$$$ "$o           oo$$$$$"               $$$$o"$o
 $    $$$$$$$  " ""oooooooooo$$$$"         o$      $$$$$$o"$
o     $$""               oo$$$"           o$$     o$$$$$$$o$
"o    $$             oo$$$$""            o$$$   o$$$$$$$$$$$
 "$o  $$$oo                           $$$$$$$   ooo$<3$$""
   "$$oooo ""            ooo$$$$      $$$$$$$$$$$$$$""
       """"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$""""""
                 """"""""""""""""""

Introduction:
Sorting Hat is a class-sorting program, designed for the Lewis & Clark College's Exploration & Discovery program. Sorting Hat allows the user to allocate first-year students to their respective mandatory classes based on a series of variables, including preferences, gender, athlete status, previous professors, and so on. 

Instructions:
0. Get a good night's sleep and a hearty breakfast. Gotta start the day right amiright? :)
1. Run sortinghat.jar.
2. Load a properly formatted (see formatting instructions below) student csv file by clicking the browse students button.
3. Load a properly formatted (see formatting instructions below) section csv file by clicking the browse sections button.
4. Check the athlete checkbox if you would like to factor athlete distribution in the sorting process.*
5. Click run to sort the students into sections!
6. The output csv file is called eanddsorted.csv, located in the user directory.**
7. 

*Note: sometimes this might affect how students receive their top choice. If you find the results to be unsatisfactory, unchecking the athlete box might improve them.
**Note: if eanddsorted.csv already exists, the output file would have a number at the end. For example, eanddsorted(2).csv

File Formatting Instructions:
To ensure the program runs properly, the student and section csv files should be accurately formatted. Headers are optional. If headers are present, they can be in any order, and capitalization is optional. Here is how:
Students: Placement,ID,Gender,Athlete,Sport,SSS,AES,Transfer,Continuing,Previous Instructor,Illegal Sections,Choice 1,Choice 2,Choice 3,Choice 4,Choice 5,Choice 6
Section: Core Section #, Professor, Student Cap
