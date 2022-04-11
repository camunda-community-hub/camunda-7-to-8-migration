const convertJuel = require("./JuelToFeelConverter");

test("Simple Expressions", () => {
    expect(convertJuel("#{x}").feelExpression).toBe("=x");
    expect(convertJuel("${x}").feelExpression).toBe("=x");
    
    expect(convertJuel("#{x>5}").feelExpression).toBe("=x>5");
    expect(convertJuel("#{x gt 5}").feelExpression).toBe("=x > 5");
    expect(convertJuel("#{x < 5}").feelExpression).toBe("=x < 5");
    expect(convertJuel("#{x lt 5}").feelExpression).toBe("=x < 5");
    
    expect(convertJuel("#{x==5}").feelExpression).toBe("=x=5");
    expect(convertJuel("#{x!=5}").feelExpression).toBe("=x!=5");
    expect(convertJuel("#{x eq 5}").feelExpression).toBe("=x = 5");
    expect(convertJuel("#{x ne 5}").feelExpression).toBe("=x != 5");

    expect(convertJuel('#{x == "test"}').feelExpression).toBe('=x = "test"');

    expect(convertJuel('#{true}').feelExpression).toBe('=true');

    // TODO
    //expect(convertJuel('#{false}').feelExpression).toBe('=false');
    //expect(convertJuel('#{!x}').feelExpression).toBe('=x = false');
    // https://camunda.github.io/feel-scala/docs/reference/builtin-functions/feel-built-in-functions-boolean#not
    //expect(convertJuel('#{!true}').feelExpression).toBe('=not(true');    
    //expect(convertJuel('#{not(x>5)}').feelExpression).toBe('=not(x>5)');

    expect(convertJuel('#{x && y}').feelExpression).toBe('=x and y');
    expect(convertJuel('#{x and y}').feelExpression).toBe('=x and y');
    expect(convertJuel('#{x || y}').feelExpression).toBe('=x or y');
    expect(convertJuel('#{x or y}').feelExpression).toBe('=x or y');

    // Empty + Not Null check?
});

notest("Objects", () => {
    // This is still to be implemented
    expect(convertJuel("#{customer.name}").feelExpression).toBe("=customer.name");
    expect(convertJuel('#{customer.address["street"]}').feelExpression).toBe("=customer.address.street");
    expect(convertJuel("#{customer.orders[1]}").feelExpression).toBe("=customer.orders[2]");
});


notest("JUEL Tutorial", () => {
    // Other elements valid in JUEL (taken from https://docs.oracle.com/javaee/5/tutorial/doc/bnahq.html)
    // Still need to be translated and implemented
    expect(convertJuel("${1 > (4/2)}").feelExpression).toBe("=xxx");
    expect(convertJuel("${4.0 >= 3}").feelExpression).toBe("=xxx");
    expect(convertJuel("${100.0 == 100}").feelExpression).toBe("=xxx");
    expect(convertJuel("${(10*10) ne 100}").feelExpression).toBe("=xxx");
    expect(convertJuel("${'a' < 'b'}").feelExpression).toBe("=xxx");
    expect(convertJuel("${'hip' gt 'hit'}").feelExpression).toBe("=xxx");
    expect(convertJuel("${4 > 3}").feelExpression).toBe("=xxx");
    expect(convertJuel("${1.2E4 + 1.4}").feelExpression).toBe("=xxx");
    expect(convertJuel("").feelExpression).toBe("=xxx");
    expect(convertJuel("").feelExpression).toBe("=xxx");
    expect(convertJuel("").feelExpression).toBe("=xxx");
});

function notest(name, test) {

}
