A gray code is a binary encoding of a number such that transitions between consecutive numbers can differ by only one bit.


Create functions to encode a positive integers ranging from 0 to 127 to and from Gray code.

There are many possible Gray codes. The following
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


[Source http://rosettacode.org]%  
