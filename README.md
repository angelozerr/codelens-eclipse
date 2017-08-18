# codelens-eclipse

It exists today 2 bugs for implementing CodeLens inside Eclipse: bug [508458](https://bugs.eclipse.org/bugs/show_bug.cgi?id=508458) and [501546](https://bugs.eclipse.org/bugs/show_bug.cgi?id=501546).

This project is a POC to study how to implement CodeLens with Eclipse StyledText

Here a demo with 

 * `TypeScript` with [typescript.java](https://github.com/angelozerr/typescript.java) :

![CodeLens TypeScript Demo](CodeLensTypeScriptDemo.gif)

 * `Java` with `JDT Java Editor` :

![CodeLens JDT Demo](CodeLensJDTDemo.gif)

 * `Java` with [lsp4e Java Editor](https://github.com/eclipselabs/language-servers-for-eclipse/tree/master/org.eclipse.lsp4e.java) which uses Java Language Server of JDT :

![CodeLens lsp4e Demo](CodeLensLSP4e.png)
 
Here a demo with the current work with StyledText where zone are added:

![CodeLens StyledText demo](CodeLensStyledText.gif)
