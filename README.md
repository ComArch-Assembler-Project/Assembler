# Assembler

- status
  - [ ] R-type
  - [ ] I-type
  - [ ] J-type
  - [ ] O-type
 
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

### Parser

Format : label instruction field0 field1 field2 comments

- R-type : add , nand
  - regA regB destReg
- I-type : lw , sw , beq
  - regA regB
- J-type : jalr
  - regA regB
- O-type : halt , noop
  - no field

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
