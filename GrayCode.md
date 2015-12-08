A gray code is a binary encoding of a number such that transitions between consecutive numbers can differ by only one bit.

For example, here is a comparison between the binary and result of your function for the decimal numbers 1 to 8:

| Decimal | Binary | Binary reflected Gray code |
|---------|--------|----------------------------|
| 1       | 0001   | 0001                       |
| 2       | 0010   | 0011                       |
| 3       | 0011   | 0010                       |
| 4       | 0100   | 0110                       |
| 5       | 0101   | 0111                       |
| 6       | 0110   | 0101                       |
| 7       | 0111   | 0100                       |
| 8       | 1000   | 1100                       |
      
Create functions to encode a positive integers ranging from 0 to 127 to and from Gray code.

There are many possible Gray codes. The one you should create
encodes what is called "binary reflected Gray code."

Encoding (MSB is bit 0, b is binary, g is Gray code):
  if b[i-1] = 1
     g[i] = not b[i]
  else
     g[i] = b[i]


Decoding (MSB is bit 0, b is binary, g is Gray code):
  b[0] = g[0]

  for other bits:
  b[i] = g[i] xor b[i-1]


There is a stub provided in GrayCode.scala, with skeleton methods filled in.

If you are comfortable with ScalaTest, a template property (that always fails) is in `graycode.GrayCodeSpec.scala`.
Otherwise, a scalacheck template (also failing) can be found in `graycode.GrayCodeProperties`.


The goal of the exercise is to create properties that specify the two functions, and get used to thinking about how to write property tests. 
It is not necessarily to solve the gray code itself, so feel free to consult google to draw inspiration.
 
[Source http://rosettacode.org]%  
