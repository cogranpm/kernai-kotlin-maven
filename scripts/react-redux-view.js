const File = Java.type('java.io.File');
const Files = Java.type('java.nio.file.Files');
const Charset = Java.type('java.nio.charset.Charset');
const Paths = Java.type('java.nio.file.Paths');
const StringWriter = Java.type('java.io.StringWriter');
const FileWriter = Java.type('java.io.FileWriter');
const HashMap = Java.type('java.util.HashMap');

const Window = Java.type('org.eclipse.jface.window.Window');

const tempOutputDirectory = `${ApplicationData.getUserPath()}${File.separator}`;
const basepath = "";

const writeTemplate = (folder, fileName, viewDef, pebbleTemplate) => {
    console.log(`writing template ${fileName}`);
    const tempOutputPath = Paths.get(tempOutputDirectory, "models", folder);
    Files.createDirectories(tempOutputPath);
    const filePath = tempOutputPath.resolve(fileName);
    const writer = new StringWriter();
    let context = new HashMap();
    context.put("viewDef", viewDef);
    pebbleTemplate.evaluate(writer, context);
    const output = writer.toString();
    const fw = new FileWriter(filePath.toString());
    fw.write(output);
    fw.close();
};

const dialogResult = ViewDefinitionSelector.open();
if (dialogResult == Window.OK) {
    if (ViewDefinitionSelector.getSelectedEntity() !== null) {
        const viewId = ViewDefinitionSelector.getSelectedEntity().getViewId();
        const def = DefaultViewDefinitions.loadView(viewId, true);
        const folder = "components";

        const viewTemplate  = ApplicationData.getPebbleEngine().getTemplate(`${basepath}reduxRouterView.peb`);
        const viewfileName = ApplicationData.makeCapital(def.getEntityDef().getName()) + ".jsx";
        writeTemplate(folder, viewfileName, def, viewTemplate);

        const formTemplate  = ApplicationData.getPebbleEngine().getTemplate(`${basepath}reduxRouterForm.peb`);
        const formfileName = ApplicationData.makeCapital(def.getEntityDef().getName()) + "Form.jsx";
        writeTemplate(folder, formfileName, def, formTemplate);

        writeTemplate(folder,
        	ApplicationData.makeCapital(def.getEntityDef().getName()) + "ReduxSliceSnippets.js",
        	def,
        	ApplicationData.getPebbleEngine().getTemplate(`${basepath}reduxSliceSnippet.peb`)
        	);
        writeTemplate("factories",
        	def.getEntityDef().getName() + "Factory.js",
        	def,
        	ApplicationData.getPebbleEngine().getTemplate(`${basepath}reduxRouterFactory.peb`)
        	);

    }
    console.log("Complete");
}

