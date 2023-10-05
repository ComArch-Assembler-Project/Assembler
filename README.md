# Assembler

### status
  - [x] R-type
  - [x] I-type
  - [x] J-type
  - [x] O-type
 
### Mapping
  - Instruction type
  - opcode
  - Field-numbers
  - Label (hashMap)


> [!IMPORTANT]
> R-type instructions (add, nand)
>
> - Bits 24-22 opcode
> - Bits 21-19 reg A (rs)
> - Bits 18-16 reg B (rt)
> - Bits 15-3 ไม่ใช้ (ควรตั้งไว้ที่ 0)
> - Bits 2-0 destReg (rd)

> [!IMPORTANT]
> I-type instructions (lw, sw, beq)
>
> - Bits 24-22 opcode
> - Bits 21-19 reg A (rs)
> - Bits 18-16 reg B (rt)
> - Bits 15-0 offsetField (เลข16-bit และเป็น 2’s complement โดยอยู่ในช่วง –32768 ถึง 32767)

> [!IMPORTANT]
> J-Type instructions (jalr)
>
> - Bits 24-22 opcode
> - Bits 21-19 reg A (rs)
> - Bits 18-16 reg B (rd)
> - Bits 15-0 ไม่ใช้ (ควรตั้งไว้ที่ 0)

> [!IMPORTANT]
> O-type instructions (halt, noop)
>
> - Bits 24-22 opcode
> - Bits 21-0 ไม่ใช้ (ควรตั้งไว้ที่ 0)

### Parser-Assembler
- Main Function
```
  - parseLineToData()       -> parse line by line to Data-list
  - LabelMapping()          -> check and map all Label
  - reset()                 -> reset pointer to start of input
  - computeToMachineCode()  -> compute all step by step (LabelCheck() >> reset() >> compute() >> return(output))
  - compute()               -> read and parse line by line & type by type to binary MachineCodes (List)
```
- Important Function
```
  - addZeroBits(field , size)     -> add 0 to field until field.Length = size
  - TwoCompliment(binary)         -> do 2's compliment on binary input
  - binaryToDecimal(List<String>) -> turn binaryList to decimalList
  - decimalToBinary(List<String>) -> turn decimalList to binaryList
  - toInteger(String)             -> turn String to Integer
```
- Check Function
```
    - isLabel(String)            -> check input is match LabelMapping or not
    - isInstruction(String)      -> check input is match InstructionMapping or not
    - isInteger(String)          -> check input is match regex or not
```
- Validation Function
```
    - LabelValidation(String)    -> check Label is in correct form
```

- The regular expression `^-\d+(\.\d+)?$` is designed to match numeric values, both integer and decimal, with an optional negative sign.

- `^`: Matches the start of a string.
- `-?`: Matches an optional minus sign (`-`). The `?` means zero or one occurrence.
- `\d+`: Matches one or more digits. `\d` represents a digit (0-9).
- `(\.\d+)?`: This is an optional group (`(...)?`) that matches a dot followed by one or more digits. This represents the decimal part of a number.
  - `\.`: Matches a literal dot.
  - `\d+`: Matches one or more digits after the dot.
- `$`: Matches the end of a string.

#### Example regular expression

- "42"
- "-3.14"
- "0.123"
- "-1000"
- "1000.00"


### Format : label instruction field[0] field[1] field[2] comments

- R-type : add , nand 
  - regA regB destReg -> (3 fields)
  - List of machineCodes = field[0] + field[1] + 0000000000000 (13-bits) + field[2]
- I-type : lw , sw , beq
  - regA regB         -> (2 fields with 1 offsetField)
    - List of machineCodes = field[0] + field[1] + offsetField(16-bits)
      - field[2] is address
      - lw , sw -> offsetField = field[2]
      - beq -> offsetField = field[2] - PC - 1
- J-type : jalr
  - regA regB         -> (2 fields)
  - List of machineCodes = regA + regB + 0000000000000000 (16-bits)
- O-type : halt , noop
  - no field          -> (0 field)
  - List of machineCodes = 0000000000000000000000 (22-bits)

### Opcode table foreach type

| Assembly Language (name of instruction) | Opcodes in binary (bits 24,23,22) | Action                                                                                                                                                                                               |
| --------------------------------------- | --------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| add (R-type format)                     | 000                               | บวก ค่าใน regA ด้วยค่าใน regB และเอาไปเก็บใน destReg                                                                                                                                                 |
| nand (R-type format)                    | 001                               | Nand ค่าใน regA ด้วยค่าใน regB และเอาค่าไปเก็บใน destReg                                                                                                                                             |
| lw (I-type format)                      | 010                               | Load regB จาก memory และ memory address หาได้จากการเอา offsetField บวกกับค่าใน regA                                                                                                                  |
| sw (I-type format)                      | 011                               | Store regB ใน memory และ memory address หาได้จากการเอา offsetField บวกกับค่าใน regA                                                                                                                  |
| beq (I-type format)                     | 100                               | ถ้า ค่าใน regA เท่ากับค่าใน regB ให้กระโดดไปที่ address PC+1+offsetField ซึ่ง PC คือ address ของ beq instruction                                                                                     |
| jalr (J-type format)                    | 101                               | เก็บค่า PC+1 ไว้ใน regB ซึ่ง PC คือ address ของ jalr instruction และกระโดดไปที่ address ที่ถูกเก็บไว้ใน regA แต่ถ้า regA และ regB คือ register ตัวเดียวกัน ให้เก็บ PC+1 ก่อน และค่อยกระโดดไปที่ PC+1 |
| bhalt (O-type format)                   | 110                               | เพิ่มค่า PC เหมือน instructions อื่นๆ และ halt เครื่อง นั่นคือให้ simulator รู้ว่าเครื่องมีการ halted เกิดขึ้น                                                                                       |
| noop (O-type format)                    | 111                               | ไม่ทำอะไรเลย                                                                                                                                                                                         |


### Output count 5 to 0 
- input
  ``` Assembly
                lw       0        1        five     load reg1 with 5 (uses symbolic address)
                lw       1        2        3        load reg2 with -1 (uses numeric address)
      start     add      1        2        1        decrement reg1
                beq      0        1        2        goto end of program when reg1 == 0
                beq      0        0        start    go back to the beginning of the loop
                noop
      done      halt                                end of program
      five      .fill    5
      neg1      .fill    -1
      stAddr    .fill    start                      will contain the address of start
  ```
- output
  ``` Decimal
      8454151
      9043971
      655361
      16842754
      16842749
      29360128
      25165824
      5
      -1
      2
  ```
