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


const writeBatchCommands = (folder, fileName, pebbleTemplate, pathsMap) => {
    print('writing template');
    const tempOutputPath = Paths.get(tempOutputDirectory, "models", folder);
    Files.createDirectories(tempOutputPath);
    const filePath = tempOutputPath.resolve(fileName);
    const writer = new StringWriter();
    let context = new HashMap();
    context.put("pathsMap", pathsMap);
    pebbleTemplate.evaluate(writer, context);
    const output = writer.toString();
    const fw = new FileWriter(filePath.toString());
    fw.write(output);
    fw.close();
 }

const getXCopyCommand = (modelFile, modelFolder, targetFolder) => {
    const targetFolderBase = "d:/projects/ConklinCentral/";
    const tempOutputPath = Paths.get(tempOutputDirectory, "models", modelFolder);
    const targetFilePath = tempOutputPath.resolve(modelFile);
    return {"sourceFile": targetFilePath, "targetFolder": targetFolderBase + targetFolder};
}
let pathsMap = [];


let modelFolder = viewDef.getEntityDef().getName() + "/Model/ConklinCentral";
let modelFile = ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + ".cs";
let modelTargetFolder = "Portal.Repository/Model/ConklinCentral";
writeTemplate(
    modelFolder,
    modelFile,
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}entity.peb`),
   viewDef.getEntityDef().getName() + "/Model/ConklinCentral");
pathsMap.push(getXCopyCommand(modelFile, modelFolder, modelTargetFolder));

let forListModelFile = ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "ForList.cs";
writeTemplate(
    modelFolder,
    forListModelFile,
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}entityForList.peb`));

pathsMap.push(getXCopyCommand(forListModelFile, modelFolder, modelTargetFolder));

let repoFolder = viewDef.getEntityDef().getName() + "/Repository";
let repoClassFile = ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "Repository.cs";
let repoTargetFolder = "Portal.Repository";
writeTemplate(
    repoFolder,
    repoClassFile,
    viewDef,
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}repositoryClass.peb`));

pathsMap.push(getXCopyCommand(repoClassFile, repoFolder, repoTargetFolder));

let serviceFolder = viewDef.getEntityDef().getName() + "/Service";
let serviceClassFile = ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "Service.cs";
let serviceTargetFolder = "Portal.Service";
writeTemplate(
    serviceFolder,
    serviceClassFile,
    viewDef,
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}service.peb`));
pathsMap.push(getXCopyCommand(serviceClassFile, serviceFolder, serviceTargetFolder));

let controllerFolder = viewDef.getEntityDef().getName() + "/Controllers"
let controllerClassFile = ApplicationData.makeCapital(viewDef.getEntityDef().getName()) +  "sController.cs";
let controllerTargetFolder = "Portal.Web/Controllers";
writeTemplate(
    controllerFolder ,
    controllerClassFile,
    viewDef,
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}controller.peb`));
pathsMap.push(getXCopyCommand(controllerClassFile, controllerFolder, controllerTargetFolder));

let viewsFolder = viewDef.getEntityDef().getName() + "/Views/" + viewDef.getEntityDef().getName() + "s";
let indexFileName = "Index.cshtml";
let viewsTargetFolder = `Portal.Web/Views/${viewDef.getEntityDef().getName()}s`;
writeTemplate(
    viewsFolder,
    indexFileName,
    viewDef,
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}index.peb`));
pathsMap.push(getXCopyCommand(indexFileName, viewsFolder, viewsTargetFolder));

let listFileName = "_list.cshtml";
writeTemplate(
    viewsFolder,
    listFileName,
    viewDef,
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}list.peb`));
pathsMap.push(getXCopyCommand(listFileName, viewsFolder, viewsTargetFolder));

let infoViewFileName = "_info.cshtml";
writeTemplate(
    viewsFolder,
    infoViewFileName,
    viewDef,
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}info.peb`));
pathsMap.push(getXCopyCommand(infoViewFileName, viewsFolder, viewsTargetFolder));


let viewModelFolder = viewDef.getEntityDef().getName() + "/ViewModel";
let listJsonFileName = ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "ListJson.cs";
let viewModelTargetFolder = "Portal.Web/Models";
writeTemplate(
    viewModelFolder,
    listJsonFileName,
    viewDef,
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}listJson.peb`));
pathsMap.push(getXCopyCommand(listJsonFileName, viewModelFolder, viewModelTargetFolder));

let infoClassName = ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "Info.cs";
writeTemplate(
    viewModelFolder,
    infoClassName,
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}viewModelInfo.peb`));
pathsMap.push(getXCopyCommand(infoClassName, viewModelFolder, viewModelTargetFolder));


let searchViewModelClassName = ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "Search.cs";
writeTemplate(
    viewModelFolder,
    searchViewModelClassName,
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}search.peb`));
pathsMap.push(getXCopyCommand(searchViewModelClassName, viewModelFolder, viewModelTargetFolder));

let searchCriteriaClassName = ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "SearchCriteria.cs";
writeTemplate(
    viewModelFolder,
    searchCriteriaClassName,
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}searchCriteria.peb`));
pathsMap.push(getXCopyCommand(searchCriteriaClassName, viewModelFolder, viewModelTargetFolder));

let wwwrootFolder = viewDef.getEntityDef().getName() + "/wwwroot/" + viewDef.getEntityDef().getName();
let listtsFileName = "list.ts";
let wwwrootTargetFolder = `Portal.Web/wwwroot/${viewDef.getEntityDef().getName()}`;
writeTemplate(
    wwwrootFolder,
    listtsFileName,
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}list_ts.peb`));
pathsMap.push(getXCopyCommand(listtsFileName, wwwrootFolder, wwwrootTargetFolder));

writeBatchCommands(
    viewDef.getEntityDef().getName(),
    'commands.bat',
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}postWriteCommands.peb`),
    pathsMap
    );

print("Completed");
