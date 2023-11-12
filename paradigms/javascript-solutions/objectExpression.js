"use strict";

Variable.VALUES = {
    'x': 0,
    'y': 1,
    'z': 2
}

function Const(value) {
    this.value = value;
}

Const.prototype = {
    evaluate: function () {
        return this.value;
    },

    toString: function () {
        return this.value.toString();
    },

    diff: function () {
        return new Const(0);
    },
    prefix: function () {
        return this.value.toString();
    }

}

function Variable(name) {
    this.name = name;
}


Variable.prototype = {
    evaluate: function (...args) {
        return args[Variable.VALUES[this.name]];
    },

    toString: function () {
        return this.name;
    },

    diff: function (varName) {
        return new Const(varName === this.name ? 1 : 0)
    },
    prefix: function () {
        return this.name;
    }


}


function Operation(sign, f, df, ...args) {
    this.sign = sign;
    this.f = f;
    this.df = df;
    this.args = args;
}

Operation.prototype = {
    evaluate: function (...args) {
        return this.f(...this.args.map(d => d.evaluate(...args)));
    },

    toString: function () {
        return [...this.args].map((e) => e.toString()).join(" ") + " " + this.sign;
    },

    diff: function (d) {
        return this.df(d, ...this.args);
    },
    prefix: function () {
        // console.log(this.args);
        return "(" + this.sign + " " + (this.args).map(arg => arg.prefix()).join(" ") + ")";
    }

}

function CreateClass(sign, f, df) {
    function AbstractClass(...args) {
        Operation.call(this, sign, f, df, ...args)
    }

    AbstractClass.prototype = Object.create(Operation.prototype)
    return AbstractClass
}


const Add = CreateClass(
    '+',
    (a, b) => a + b,
    (d, ...args) => new Add(args[0].diff(d), args[1].diff(d)))

const Subtract = CreateClass(
    '-',
    (a, b) => a - b,
    (d, ...args) => new Subtract(args[0].diff(d), args[1].diff(d))
)

const Multiply = CreateClass(
    '*',
    (a, b) => a * b,
    (d, ...args) =>
        new Add(new Multiply(args[0].diff(d), args[1]),
            new Multiply(args[0], args[1].diff(d)))
)

const Divide = CreateClass(
    '/',
    (a, b) => a / b,
    (d, ...args) =>
        new Divide(new Subtract(new Multiply(args[0].diff(d), args[1]),
            new Multiply(args[0], args[1].diff(d))), new Multiply(args[1], args[1]))
)

const Negate = CreateClass(
    'negate',
    (a) => -a,
    (d, ...args) => new Negate(args[0].diff(d))
);

const ArcTan = CreateClass(
    'atan',
    (a) => Math.atan(a),
    (d, ...args) => new Divide(
        args[0].diff(d), new Add(
            new Const(1), new Multiply(args[0], args[0]))
    ));

const ArcTan2 = CreateClass(
    "atan2",
    (a, b) => Math.atan2(a, b),
    (d, ...args) => {
        const a = args[0];
        const b = args[1];
        return new Divide(
            new Subtract(new Multiply(b, a.diff(d)), new Multiply(a, b.diff(d))),
            new Add(new Multiply(a, a), new Multiply(b, b))
        );
    });

const Sum = CreateClass(
    'sum',
    (...args) => args.reduce((a, b) => (a + b), 0),
    (d, ...args) => (0)
)

const Avg = CreateClass(
    'avg',
    (...args) => (args.reduce((a, b) => (a + b)) / args.length),
    (d, ...args) => (0)
)



const binaryOperations = {
    '+': Add,
    '-': Subtract,
    '*': Multiply,
    '/': Divide,
    'atan2': ArcTan2
};

const unaryOperations = {
    'negate': Negate,
    'atan': ArcTan
};

const nAryOperations = {
    'sum': Sum,
    'avg': Avg
};


function parse(expression) {
    const stack = [];
    const tokens = expression.trim().split(/\s+/);


    tokens.forEach(token => {
        if (token in binaryOperations) {
            const right = stack.pop();
            const left = stack.pop();
            stack.push(new binaryOperations[token](left, right));
        } else if (token in unaryOperations) {
            const operand = stack.pop();
            stack.push(new unaryOperations[token](operand));
        } else if (/^[xyz]$/.test(token)) {
            stack.push(new Variable(token));
        } else {
            stack.push(new Const(Number(token)));
        }
    });

    return stack.pop();
}

function ParseError(message) {
    this.message = message
    Error.call(this, message)
}

ParseError.prototype = Object.create(Error.prototype);
ParseError.prototype.constructor = ParseError


function parsePrefixRealization(expression) {
    const tokens = expression.trim().split(/(\(|\)|\s+)/).filter(token => token.trim() !== '');

    let index = 0;

    function parseToken() {
        const token = tokens[index++];

        if (token === undefined) {
            throw new ParseError('Missing input');
        } else if (token === '(') {
            const op = tokens[index++];

            let args = [];
            while (tokens[index] !== ')') {
                args.push(parseToken());
            }
            index++;

            if (op in binaryOperations) {
                if (args.length !== 2) {
                    throw new ParseError(`Expected 2 arguments for binary operation '${op}', but found ${args.length}`);
                }
                return new binaryOperations[op](args[0], args[1]);
            } else if (op in unaryOperations) {
                if (args.length !== 1) {
                    throw new ParseError(`Expected 1 argument for unary operation '${op}', but found ${args.length}`);
                }
                return new unaryOperations[op](args[0]);
            } else if (op in nAryOperations) {
                return new nAryOperations[op](...args.splice(-args.length))
            } else {
                throw new ParseError(`Unknown operation '${op}'`);
            }
        } else if (token === ')') {
            throw new ParseError(`Unexpected token ')'`);
        } else if (token in binaryOperations) {
            throw new ParseError(`Unexpected binary operation '${token}'`);
        } else if (token in unaryOperations) {
            const operand = parseToken();
            return new unaryOperations[token](operand);
        } else if (/^[xyz]$/.test(token)) {
            return new Variable(token);
        } else if (isFinite(token) && !isNaN(token)) {
            return new Const(Number(token));
        } else {
            throw new ParseError(`Unexpected token "${token}"`);
        }
    }

    const result = parseToken();

    if (index !== tokens.length) {
        throw new ParseError(`Unexpected tokens at the end of input`);
    }

    return result;
}

function parsePrefix(expr) {
    try {
        return parsePrefixRealization(expr)
    } catch (e) {
        throw e
    }
}
// let expr = parsePrefix('(sum x)');
// console.log((+ 0.1 0.2))