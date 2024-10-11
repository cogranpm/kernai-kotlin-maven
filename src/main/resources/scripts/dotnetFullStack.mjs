const File = Java.type('java.io.File');
const Files = Java.type('java.nio.file.Files');
const Charset = Java.type('java.nio.charset.Charset');
const Paths = Java.type('java.nio.file.Paths');
const StringWriter = Java.type('java.io.StringWriter');
const FileWriter = Java.type('java.io.FileWriter');
const HashMap = Java.type('java.util.HashMap');
const ArrayList = Java.type('java.util.ArrayList');

const tempOutputDirectory = `${ApplicationData.getUserPath()}${File.separator}`;
const basepath = "";

const writeTemplate = (folder, fileName, viewDef, pebbleTemplate) => {
    print('writing template');
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

writeTemplate(
    "DTO",
    ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "DTO.cs",
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}dto.peb`));
print(`Wrote File ${ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "DTO.cs"}`);

writeTemplate(
    "Grpc",
    ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "Service.proto",
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}protocolBuffer.peb`));
print(`Wrote File ${ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "Service.proto"}`);
