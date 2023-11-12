"use strict"


let operation = f => (...args) => (...variables) => f(...args.map(a => a(...variables)))


let add = operation((a, b) => (a + b))

let subtract = operation((a, b) => (a - b))

let multiply = operation((a, b) => (a * b))

let divide = operation((a, b) => (a / b))

let negate = operation(a => -a)

let cnst = a => () => a

let sinh = operation(Math.sinh)

let cosh = operation(Math.cosh)

let variable = arg => (...variables) => variables[map[arg]]

let map = {
    "x": 0,
    "y": 1,
    "z": 2
}

let x = variable("x")
let one = cnst(1)
let two = cnst(2)
let e = cnst(Math.E)
let pi = cnst(Math.PI)

let testExpr = add(subtract(multiply(x, x), multiply(two, x)), one);

for (let i = 0; i <= 10; i++) {
    console.log(testExpr(i, 0, 0));
}

