var BigDecimal = Java.type('java.math.BigDecimal');
var bd = new BigDecimal('10');

var MyIterator = Java.extend(java.util.Iterator, { hasNext() { return true; }, next() { return 42; }});
var iter = new MyIterator();
print(iter.next());

var Display = Java.type('org.eclipse.swt.widgets.Display');
var Default = Display.getDefault();
var SWT = Java.type('org.eclipse.swt.SWT')
var ApplicationWindow = Java.type('org.eclipse.jface.window.ApplicationWindow')
var Composite = Java.type('org.eclipse.swt.widgets.Composite');
var  MainWin = Java.extend(ApplicationWindow, { createContents(parent) { return new Composite(parent, SWT.NONE); }});
var theWin = new MainWin(null);
theWin.setBlockOnOpen(true)
theWin.open()
print(theWin);
print('hello javascript, number');