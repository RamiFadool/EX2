# Spreadsheet Implementation (Ex2) 📝

##  Project Overview
In this project will design and implement a basic version of a Spreadsheet, which is a 2D array of “Cells”, each cell can be a String (aka Text), or a number (aka Double) or a Formula. 

## Features
- cell evaluation
- Formula processing with mathematical operations
- Cell referencing (e.g., A1, B2)
- Error detection (cycles, invalid formulas)
- File save/load
- Depth calculation for formula dependencies

## Cell Types
1. **Numbers** 🔢
   - `1.0`, `3.14`, `100.5`, ...

2. **Text** (•‿•)
   - `Hello`, `dsvzdsgd`

3. **Formulas** ➕➖✖️➗
   - Basic: `=1`, `=A1`
   - Operations: `=1+2`, `=A1*B2`
   - Complex: `=(A1+B2)*((C3))-1`

## Formula Rules
Valid Examples: ✅
  - =1, =1.2, =(0.2), =1+2, =1+2*3, =(1+2)*((3))-1, =A1, =A2+3, =(2+A3)/A2,

Invalid Cases: ❌
  - a, AB, @2, 2+), (3+1*2)-, =(), =5**,

## Testing
JUnit tests that cover:

- Formula evaluation
- Cell operations
- Error handling
- Edge cases
