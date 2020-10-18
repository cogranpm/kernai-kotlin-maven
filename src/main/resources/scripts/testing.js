// testing the use of bindings
// can pass in objects from the graal context
// foo is a global object in the script passed from the context
print(foo)


// use a java object by first putting type declaration in a variable
var BigDecimal = Java.type('java.math.BigDecimal');
// create instance of the java type in 2 step process
var bd = new BigDecimal('10');

// example of extending from a java class and implement some methods
// 2 step process, declare extending type into variable
// then create instance of that type
var MyIterator = Java.extend(java.util.Iterator, { hasNext() { return true; }, next() { return 42; }});
var iter = new MyIterator();
print(iter.next());

// java maps
var HashMap = Java.type('java.util.HashMap');
var map = new HashMap();
map.put(1, "a");
map.get(1);

// iterate map
for (var key in map) {
    print(key);
    print(map.get(key));
}


//In GraalVM JavaScript you can create and access Java Lists, e.g. java.util.ArrayList.
var ArrayList = Java.type('java.util.ArrayList');
var list = new ArrayList();
list.add(42);
list.add("23");
list.add({});

for (var idx in list) {
    print(idx);
    print(list.get(idx));
}

//Exceptions thrown in Java code can be caught in JavaScript code. They are represented as Java objects.

try {
    Java.type('java.lang.Class').forName("nonexistent");
} catch (e) {
    print(e.getMessage());
}

// testing some swt stuff
var Display = Java.type('org.eclipse.swt.widgets.Display');
var Default = Display.getDefault();
var SWT = Java.type('org.eclipse.swt.SWT')
var ApplicationWindow = Java.type('org.eclipse.jface.window.ApplicationWindow')
var Composite = Java.type('org.eclipse.swt.widgets.Composite');
var  MainWin = Java.extend(ApplicationWindow, { createContents(parent) { return new Composite(parent, SWT.NONE); }});


// check some javascript features
const the_best = "the best"
let the_worst = "the worst"

class Rectangle {
  constructor(height, width) {
    this.height = height;
    this.width = width;
  }
}

const rect = new Rectangle(500, 400)

// define a member function that java client can execute
function showWindow() {
    var theWin = new MainWin(null);
    theWin.setBlockOnOpen(true)
    theWin.open()
}

print('hello javascript, number ' + the_best + ' ' + the_worst);