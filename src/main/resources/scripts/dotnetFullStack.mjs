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
    viewDef.getEntityDef().getName() + "/Model/ConklinCentral",
    ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + ".cs",
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}entity.peb`));

writeTemplate(
    viewDef.getEntityDef().getName() + "/Model/ConklinCentral",
    ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "ForList.cs",
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}entityForList.peb`));

writeTemplate(
    viewDef.getEntityDef().getName() + "/Repository",
    ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "Repository.cs",
    viewDef,
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}repositoryClass.peb`));

writeTemplate(
    viewDef.getEntityDef().getName() + "/Service",
    ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "Service.cs",
    viewDef,
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}service.peb`));

writeTemplate(
    viewDef.getEntityDef().getName() + "/Controllers",
   ApplicationData.makeCapital(viewDef.getEntityDef().getName()) +  "sController.cs",
    viewDef,
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}controller.peb`));

writeTemplate(
    viewDef.getEntityDef().getName() + "/Views/" + viewDef.getEntityDef().getName() + "s",
    "Index.cshtml",
    viewDef,
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}index.peb`));

writeTemplate(
    viewDef.getEntityDef().getName() + "/Views/" + viewDef.getEntityDef().getName() + "s",
    "_list.cshtml",
    viewDef,
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}list.peb`));

writeTemplate(
    viewDef.getEntityDef().getName() + "/ViewModel",
    ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "ListJson.cs",
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}listJson.peb`));

writeTemplate(
    viewDef.getEntityDef().getName() + "/ViewModel",
    ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "Search.cs",
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}search.peb`));

writeTemplate(
    viewDef.getEntityDef().getName() + "/ViewModel",
    ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "SearchCriteria.cs",
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}searchCriteria.peb`));

writeTemplate(
    viewDef.getEntityDef().getName() + "/wwwroot/" + viewDef.getEntityDef().getName(),
    ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "list.ts",
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}list_ts.peb`));

print("Completed");
/*
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
*/