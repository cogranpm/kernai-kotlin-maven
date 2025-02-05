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
const targetFolderBase = "d:/projects/ConklinCentral/"; //note this should be environmental

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

const writeTemplateChild = (folder, fileName, parentViewDef, childViewDef, pebbleTemplate) => {
    print('writing template');
    const tempOutputPath = Paths.get(tempOutputDirectory, "models", folder);
    Files.createDirectories(tempOutputPath);
    const filePath = tempOutputPath.resolve(fileName);
    const writer = new StringWriter();
    let context = new HashMap();
    context.put("parentViewDef", parentViewDef);
    context.put("childViewDef", childViewDef);
    pebbleTemplate.evaluate(writer, context);
    const output = writer.toString();
    const fw = new FileWriter(filePath.toString());
    fw.write(output);
    fw.close();
};

const writeBatchCommands = (folder, fileName, pebbleTemplate, pathsMap, roboCopies) => {
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
    fw.write(roboCopies);
    fw.close();
 }

const getXCopyCommand = (modelFile, modelFolder, targetFolder) => {

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
let repoClassBaseFile = ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "RepositoryBase.cs";
let repoTargetFolder = "Portal.Repository";
writeTemplate(
    repoFolder,
    repoClassBaseFile,
    viewDef,
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}repositoryBaseClass.peb`));

pathsMap.push(getXCopyCommand(repoClassBaseFile, repoFolder, repoTargetFolder));

let repoClassFile = ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "Repository.cs";
writeTemplate(
    repoFolder,
    repoClassFile,
    viewDef,
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}repositoryClass.peb`));

//pathsMap.push(getXCopyCommand(repoClassFile, repoFolder, repoTargetFolder));


let serviceFolder = viewDef.getEntityDef().getName() + "/Service";
let serviceClassFile = ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "Service.cs";
let serviceTargetFolder = "Portal.Service";
writeTemplate(
    serviceFolder,
    serviceClassFile,
    viewDef,
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}service.peb`));
pathsMap.push(getXCopyCommand(serviceClassFile, serviceFolder, serviceTargetFolder));


const viewBaseFolderName = ApplicationData.makeCapital(viewDef.getId());

let controllerFolder = viewBaseFolderName + "/Controllers"
let controllerClassFile = viewBaseFolderName +  "sController.cs";
let controllerTargetFolder = "Portal.Web.Management/Controllers";
writeTemplate(
    controllerFolder ,
    controllerClassFile,
    viewDef,
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}controller.peb`));
pathsMap.push(getXCopyCommand(controllerClassFile, controllerFolder, controllerTargetFolder));

let viewsFolder = viewBaseFolderName + "/Views/" + viewDef.getEntityDef().getName() + "s";
let indexFileName = "Index.cshtml";
let viewsTargetFolder = `Portal.Web.Management/Views/${viewBaseFolderName}s`;
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



let viewModelFolder = viewBaseFolderName + "/ViewModel";
let listJsonFileName = viewBaseFolderName + "ListJson.cs";
let viewModelTargetFolder = "Portal.Web.Management/Models/App";
writeTemplate(
    viewModelFolder,
    listJsonFileName,
    viewDef,
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}listJson.peb`));
pathsMap.push(getXCopyCommand(listJsonFileName, viewModelFolder, viewModelTargetFolder));

let infoClassName = viewBaseFolderName + "Edit.cs";
writeTemplate(
    viewModelFolder,
    infoClassName,
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}viewModelInfo.peb`));
pathsMap.push(getXCopyCommand(infoClassName, viewModelFolder, viewModelTargetFolder));


let searchViewModelClassName = viewBaseFolderName + "Search.cs";
writeTemplate(
    viewModelFolder,
    searchViewModelClassName,
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}search.peb`));
pathsMap.push(getXCopyCommand(searchViewModelClassName, viewModelFolder, viewModelTargetFolder));

/*
let searchCriteriaClassName = ApplicationData.makeCapital(viewDef.getEntityDef().getName()) + "SearchCriteria.cs";
writeTemplate(
    viewModelFolder,
    searchCriteriaClassName,
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}searchCriteria.peb`));
pathsMap.push(getXCopyCommand(searchCriteriaClassName, viewModelFolder, viewModelTargetFolder));
*/

let wwwrootFolder = viewBaseFolderName + "/wwwroot/" + viewDef.getEntityDef().getName();
let listtsFileName = "list.ts";
let wwwrootTargetFolder = `Portal.Web.Management/wwwroot/${viewBaseFolderName}`;
writeTemplate(
    wwwrootFolder,
    listtsFileName,
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}list_ts.peb`));
pathsMap.push(getXCopyCommand(listtsFileName, wwwrootFolder, wwwrootTargetFolder));

/***************************************
child views
 */
if(viewDef.getChildViews()){
    for (let childViewDef of viewDef.getChildViews()) {

        let childViewId = childViewDef.getId();
        childViewId = ApplicationData.decapitalize(childViewId);

        let childIndexFileName = `${childViewId}Index.cshtml`;
        let viewsTargetFolder = `Portal.Web.Management/Views/${viewBaseFolderName}s`;
        writeTemplateChild(
            viewsFolder,
            childIndexFileName,
             viewDef,
            childViewDef,
            ApplicationData.getPebbleEngine().getTemplate(`${basepath}childIndex.peb`));
        pathsMap.push(getXCopyCommand(childIndexFileName, viewsFolder, viewsTargetFolder));

        let childInfoViewFileName = `_${childViewId}Info.cshtml`;
        writeTemplateChild(
            viewsFolder,
            childInfoViewFileName,
            viewDef,
            childViewDef,
            ApplicationData.getPebbleEngine().getTemplate(`${basepath}childInfo.peb`));
        pathsMap.push(getXCopyCommand(childInfoViewFileName, viewsFolder, viewsTargetFolder));

        let childListFileName = `_${childViewId}List.cshtml`;
        writeTemplateChild(
            viewsFolder,
            childListFileName,
            viewDef,
            childViewDef,
            ApplicationData.getPebbleEngine().getTemplate(`${basepath}childList.peb`));
        pathsMap.push(getXCopyCommand(childListFileName, viewsFolder, viewsTargetFolder));

        let childScriptFileName = `${childViewId}.ts`;
        writeTemplateChild(
            wwwrootFolder,
            childScriptFileName,
            viewDef,
            childViewDef,
           ApplicationData.getPebbleEngine().getTemplate(`${basepath}childlist_ts.peb`));
        pathsMap.push(getXCopyCommand(childScriptFileName, wwwrootFolder, wwwrootTargetFolder));

    }
}

let roboCopies = "";
const tempOutputPath = Paths.get(tempOutputDirectory, "models", repoFolder);
const filePath = tempOutputPath.resolve(repoClassFile);

//roboCopies = `robocopy ${filePath} ${targetFolderBase}${repoTargetFolder}/${repoClassFile} /E /XC /XN /XO`;
roboCopies = `echo n | copy /-y "${filePath}" "${targetFolderBase}${repoTargetFolder}/${repoClassFile}"`;
writeBatchCommands(
    viewDef.getEntityDef().getName(),
    'commands.bat',
    ApplicationData.getPebbleEngine().getTemplate(`${basepath}postWriteCommands.peb`),
    pathsMap,
    roboCopies
    );

let sqlFolder = viewDef.getEntityDef().getName() + "/SQL";
let tableFile = "schema.sql";
writeTemplate(
    sqlFolder,
    tableFile,
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}sqlTable.peb`));

let insertFile = "insert.sql";
writeTemplate(
    sqlFolder,
    insertFile,
    viewDef,
   ApplicationData.getPebbleEngine().getTemplate(`${basepath}sqlInsert.peb`));


print("Completed");
