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

    // ???
    //expect(convertJuel('#{!true}').feelExpression).toBe('=');    
    //expect(convertJuel('#{not (x>5)}').feelExpression).toBe('=');

    expect(convertJuel('#{x && y}').feelExpression).toBe('=x and y');
    expect(convertJuel('#{x and y}').feelExpression).toBe('=x and y');
    expect(convertJuel('#{x || y}').feelExpression).toBe('=x or y');
    expect(convertJuel('#{x or y}').feelExpression).toBe('=x or y');

    // Empty + Not Null check?
});

notest("Objects", () => {
    expect(convertJuel("#{customer.name}").feelExpression).toBe("=customer.name");
    expect(convertJuel('#{customer.address["street"]}').feelExpression).toBe("=customer.address.street");
    expect(convertJuel("#{customer.orders[1]}").feelExpression).toBe("=customer.orders[2]");
});


notest("JUEL Tutorial", () => {
    // https://docs.oracle.com/javaee/5/tutorial/doc/bnahq.html
    expect(convertJuel("${1 > (4/2)}").feelExpression).toBe("=customer.name");
    expect(convertJuel("${4.0 >= 3}").feelExpression).toBe("=customer.name");
    expect(convertJuel("${100.0 == 100}").feelExpression).toBe("=customer.name");
    expect(convertJuel("${(10*10) ne 100}").feelExpression).toBe("=customer.name");
    expect(convertJuel("${'a' < 'b'}").feelExpression).toBe("=customer.name");
    expect(convertJuel("${'hip' gt 'hit'}").feelExpression).toBe("=customer.name");
    expect(convertJuel("${4 > 3}").feelExpression).toBe("=customer.name");
    expect(convertJuel("${1.2E4 + 1.4}").feelExpression).toBe("=customer.name");
    expect(convertJuel("").feelExpression).toBe("=customer.name");
    expect(convertJuel("").feelExpression).toBe("=customer.name");
    expect(convertJuel("").feelExpression).toBe("=customer.name");
});

function notest(name, test) {

}
