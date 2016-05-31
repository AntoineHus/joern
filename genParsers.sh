#!/bin/bash
antlr4='java -jar ./lib/antlr4-4.5.3.jar'

$antlr4 src/antlr/C/Module.g4
$antlr4 src/antlr/C/Function.g4

