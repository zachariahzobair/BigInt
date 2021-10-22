import java.time.temporal.Temporal;
import java.util.Arrays;

/* 
 * BigInt.java
 * Zachariah Zobair
 * zzobair@bu.edu 
 * 
 * A class for objects that represent non-negative integers of 
 * up to 20 digits.
 */

public class BigInt  {
    // the maximum number of digits in a BigInt -- and thus the length
    // of the digits array
    private static final int SIZE = 20;
    
    // the array of digits for this BigInt object
    private int[] digits;
    
    // the number of significant digits in this BigInt object
    private int numSigDigits;

    /*
     * Default, no-argument constructor -- creates a BigInt that 
     * represents the number 0.
     */
    public BigInt() {
        this.digits = new int[SIZE];
        this.numSigDigits = 1;  // 0 has one sig. digit--the rightmost 0!
    }

    // Constructor that takes an integer argument and creates a BigInt with that value
    public BigInt(int n) { 

        if(n < 0) { //Ensuring a positive value is entered
            throw new IllegalArgumentException(); 
        }

        int numDiv = n; 
        int lengthOfNum = 0; 
        int[] backwardsNum = new int[SIZE]; // Array that is going to hold the individual digit values, albeit backwards

        while(numDiv > 0) { 
            backwardsNum[lengthOfNum] = numDiv % 10; //put the furthest right digit in the array
            lengthOfNum++; // increment the length of num, used for numSigDigits
            numDiv /= 10; // removes the rightmost digit from the number
        }
        int[] bigIntArr = new int[SIZE]; 
        int startPoint = SIZE - lengthOfNum; 

        for(int i = lengthOfNum - 1; i >= 0; i--) { 
            bigIntArr[SIZE-i-1] = backwardsNum[i]; // Reverses the backwardsNum, and puts it at the end of a new array
        }

        if(validateDigits(bigIntArr)) {
            this.digits = bigIntArr; 
            this.numSigDigits = lengthOfNum; 
        } else { 
            throw new IllegalArgumentException(); 
        }

    }

    // Constructor for an array input to create a new BigInt object with the digits of the array
    public BigInt(int[] arr) { 
        int[] bigArr = new int[20]; 
        int size = 0;
        int lengthOfNum = 1; 
        if(validateDigits(arr)) { // Ensuring that the digits entered are valid 
            size = arr.length; // size of the number, i.e. length
            int startPoint = bigArr.length - size; //what index to start in the BigInt object array
            for(int i = 0; i < arr.length; i++) { 
                bigArr[startPoint + i] = arr[i];  //copy the arr to a BigInt type array
            }
            int index = 0; 
            for(int i = 0; i < SIZE-1; i++) { // This for loop is used for calculated the sigDigs
                if(bigArr[i] != 0) {
                    index = i; 
                    break; 
                }
            }

            lengthOfNum = SIZE - index; 

            this.digits = bigArr; 
        } else 
            throw new IllegalArgumentException(); 
        
        this.numSigDigits = lengthOfNum; 
        
    }

    // Accessor method for numSigDigits
    public int getNumSigDigits() { 
        return numSigDigits; 
    }

    // Accessor method for digits
    public int[] getDigits() { 
        return digits; 
    }

    //helper method that validates that each element of given array is within [0,10), and the whole array is less than 20 digits
    private boolean validateDigits(int[] arr) { 
        boolean isValid = true; 
        for(int i : arr) { 
            if(i >= 10 || i < 0 || arr.length > 20) { 
                isValid = false;
            }
        }
        return isValid; 
    }


    // toString method converting the digits array to a String of the number
    public String toString() { 
        int start = SIZE - 1; 
        for(int i = 0; i < SIZE; i++) { 
            if(this.digits[i] != 0) {
                start = i; 
                break;
            }
        }

        String finalString = "";

        for(int i = start; i < SIZE; i++) { 
            finalString = finalString + this.digits[i]; 
        }
        return finalString;
    }


    // compareTo method that takes in a 2nd BigInt object "other" for 
    public int compareTo(BigInt other) { 
        if(other == null) {
            throw new IllegalArgumentException(); 
        } else { 
            if(this.getNumSigDigits() > other.getNumSigDigits())
                return 1; 
            else if (this.getNumSigDigits() < other.getNumSigDigits())
                return -1; 
            else { 
                int sigDig = this.getNumSigDigits(); 
                for(int i = SIZE-sigDig; i < SIZE; i++) { 
                    if(this.digits[i] > other.getDigits()[i]) {
                        return 1; 
                    } else if (this.digits[i] < other.getDigits()[i]) { 
                        return -1; 
                    }
                }
                return 0; 
            }
         }
    }

    //Method to add together two BigInt objects, resulting in a new BigInt that == the sum 
    public BigInt add(BigInt other) { 
        BigInt smallerNum, biggerNum;
        int[] resultArr = new int[SIZE]; 
        if(other == null) { 
            throw new IllegalArgumentException();
        } else { 
            
            if(this.compareTo(other) == -1) { //Indentify which number is smaller and which is larger
                smallerNum = other; 
                biggerNum = this;
            } else { 
                smallerNum = this; 
                biggerNum = other; 
            }

            int carry = 0; 
            int tempSum = 0; 

            
            for(int i = SIZE - 1; i >= (SIZE - smallerNum.getNumSigDigits()-1); i--) {
                try { 
                    tempSum = smallerNum.getDigits()[i] + biggerNum.getDigits()[i];
                    int x = (tempSum % 10 + carry); 

                    if(x < 10) { 
                        resultArr[i] = (tempSum % 10 + carry); 
                        carry = (tempSum / 10);
                    } else { 
                        resultArr[i] = (x % 10);
                        carry = x / 10; 
                    }
                } catch (IndexOutOfBoundsException e) { 
                    throw new ArithmeticException(); 
                }
            }

            // System.out.println(Arrays.toString(resultArr));

        }

        return new BigInt(resultArr); 
    }

    // Method that takes in a BigInt Object other and returns a new BigInt object that = the product of this and other
    public BigInt mul(BigInt other) {
        BigInt smallerNum, biggerNum; 
        int[] resultArr = new int[SIZE]; 
        BigInt finalResult = new BigInt(); 
        if(other == null) {
            throw new IllegalArgumentException();
        } else { 
            try { 
                if(other.getNumSigDigits() == 1 && other.getDigits()[SIZE - 1] == 1) {
                    return new BigInt(this.getDigits()); 
                }

                if(this.compareTo(other) == 1) { //Indentify which number is smaller and which is larger
                    smallerNum = other; 
                    biggerNum = this;
                } else { 
                    smallerNum = this; 
                    biggerNum = other; 
                }

                // go right -> left each digit in smaller num
                // multiply that digit times each number in larger num , store result in a BigInt value "result"
                // Add that result to a final result counter
                // each time we move on to next digit in smaller num, add a 0 to the end of the BigInt result
                // return that result big int 
                int count = 0; 
                for(int i = SIZE - 1; i >= (SIZE - smallerNum.getNumSigDigits()); i--) {
                // System.out.println("Digit: " + smallerNum.getDigits()[i]);
                    resultArr = smallerNum.helpWithMultiply(smallerNum.getDigits()[i], biggerNum, count);
                // System.out.println(Arrays.toString(resultArr));
                    finalResult = finalResult.add(new BigInt(resultArr)); 
                    count++; 
                }
            } catch (IndexOutOfBoundsException e) {
                throw new ArithmeticException(); 
            }

        }

        return finalResult; 
    }

    // Helper method to handle multiplication of a single digit and a number of multiple digits
    private int[] helpWithMultiply(int n, BigInt b, int offset) {
        int[] fin = new int[SIZE]; 
        int carry = 0; 
        for(int i = SIZE-1; i >= (SIZE-b.getNumSigDigits()-1); i--) {
            int tempProduct = ((n * b.getDigits()[i])); 
            // System.out.println(tempProduct);
            int remainder = tempProduct % 10;
            
            int x = remainder + carry; 
            if( x < 10) { 
                fin[i-offset] = (x); 
                carry = tempProduct / 10; 
            } else { 
                fin[i-offset] = x % 10; 
                carry = x / 10; 
            }
            
        }

        return fin; 
    }


    public static void main(String [] args) {
        System.out.println("Unit tests for the BigInt class.");
        System.out.println();


        /* 
         * You should uncomment and run each test--one at a time--
         * after you build the corresponding methods of the class.
         */

        
        System.out.println("Test 1: result should be 7");
        int[] a1 = { 1,2,3,4,5,6,7, };
        BigInt b1 = new BigInt(a1);
        System.out.println(b1.getNumSigDigits());
        System.out.println();

        
        System.out.println("Test 2: result should be 1234567");
        b1 = new BigInt(a1);
        System.out.println(b1);
        System.out.println();

        
        System.out.println("Test 3: result should be 0");
        int[] a2 = { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 };
        BigInt b2 = new BigInt(a2);
        System.out.println(b2);
        System.out.println();
        
        System.out.println("Test 4: should throw an IllegalArgumentException");
        try {
            int[] a3 = { 0,0,0,0,23,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 };
            BigInt b3 = new BigInt(a3);
            System.out.println("Test failed.");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed.");
        } catch (Exception e) {
            System.out.println("Test failed: threw wrong type of exception.");
        }
        System.out.println();

        
        System.out.println("Test 5: result should be 1234567");
        b1 = new BigInt(1234567);
        System.out.println(b1);
        System.out.println();

        System.out.println("Test 6: result should be 0");
        b2 = new BigInt(0);
        System.out.println(b2);
        System.out.println();

        
        System.out.println("Test 7: should throw an IllegalArgumentException");
        try {
            BigInt b3 = new BigInt(-4);
            System.out.println("Test failed.");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed.");
        } catch (Exception e) {
            System.out.println("Test failed: threw wrong type of exception.");
        }
        System.out.println();

        System.out.println("Test 8: result should be 0");
        b1 = new BigInt(12375);
        b2 = new BigInt(12375);
        System.out.println(b1.compareTo(b2));
        System.out.println();

        System.out.println("Test 9: result should be -1");
        b2 = new BigInt(12378);
        System.out.println(b1.compareTo(b2));
        System.out.println();

        System.out.println("Test 10: result should be 1");
        System.out.println(b2.compareTo(b1));
        System.out.println();

        System.out.println("Test 11: result should be 0");
        b1 = new BigInt(0);
        b2 = new BigInt(0);
        System.out.println(b1.compareTo(b2));
        System.out.println();

        
        System.out.println("Test 12: result should be\n123456789123456789");
        int[] a4 = { 3,6,1,8,2,7,3,6,0,3,6,1,8,2,7,3,6 };
        int[] a5 = { 8,7,2,7,4,0,5,3,0,8,7,2,7,4,0,5,3 };
        BigInt b4 = new BigInt(a4);
        BigInt b5 = new BigInt(a5);
        BigInt sum = b4.add(b5);
        System.out.println(sum);
        System.out.println();

        
        System.out.println("Test 13: result should be\n123456789123456789");
        System.out.println(b5.add(b4));
        System.out.println();

        System.out.println("Test 14: result should be\n3141592653598");
        b1 = new BigInt(0);
        int[] a6 = { 3,1,4,1,5,9,2,6,5,3,5,9,8 };
        b2 = new BigInt(a6);
        System.out.println(b1.add(b2));
        System.out.println();

        System.out.println("Test 15: result should be\n10000000000000000000");
        int[] a19 = { 9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9 };    // 19 nines!
        b1 = new BigInt(a19);
        b2 = new BigInt(1);
        System.out.println(b1.add(b2));
        System.out.println();

        
        System.out.println("Test 16: should throw an ArithmeticException");
        int[] a20 = { 9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9 };  // 20 nines!
        try {
            b1 = new BigInt(a20);
            System.out.println(b1.add(b2));
        } catch (ArithmeticException e) {
            System.out.println("Test passed.");
        } catch (Exception e) {
            System.out.println("Test failed: threw wrong type of exception.");
        }
        System.out.println();
        
       System.out.println("Test 17: result should be 5670");
        b1 = new BigInt(135);
        b2 = new BigInt(42);
        BigInt product = b1.mul(b2);
        System.out.println(product);
        System.out.println();

       
        System.out.println("Test 18: result should be\n99999999999999999999");
        b1 = new BigInt(a20);   // 20 nines -- see above
        b2 = new BigInt(1);
        System.out.println(b1.mul(b2));
        System.out.println();
 
        System.out.println("Test 19: should throw an ArithmeticException");
        try {
            b1 = new BigInt(a20);
            b2 = new BigInt(2);
            System.out.println(b1.mul(b2));
        } catch (ArithmeticException e) {
            System.out.println("Test passed.");
        } catch (Exception e) {
            System.out.println("Test failed: threw wrong type of exception.");
        }
        System.out.println();

        /*
        */
    }
}