# MIDL compiler

## 1. compiler design 

### 1.1 Token Analysis

+ According to the regular expressions of different Token types, design the NFA firstly.
+ transform NFA to DFA.
+ use double switch-case to implement state transformation in DFA. 

### 1.2 Syntax Analysis

+ design abstract syntax tree
+ use recursive descent to implement the context-free grammars.
+ generate syntax tree to text files.

## 2. usage

### 2.1 requirement

+ IDEA 
+ Java1.8

### 2.2 script

+ put MIDL code files into `inputFile folder`
+ run CompilerTest.java in `src folder`

