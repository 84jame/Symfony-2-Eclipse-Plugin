1.1.1

* Service class name refactoring support
* Move annotation validator to Doctrine plugin
* Use Annotation cache from Doctrine plugin
* Improved doctrine entity manager resolver

1.1.0

* Doctrine become standalone feature

1.0.94

* Project wizard now uses composers `create-project` command
* Project wizard can now automatically create Run/Debug configurations for new projects
* Project wizard can now automatically create a local server for new projects
* Added help contexts for wizards
* Added an "Import Symfony project" wizard to import existing projects and provide a proper project setup
* The Dumped service container used for service typeinference can now be selected in the project properties page
* Added codeassist tests for: Annotations, Doctrine, Service Typeinference, Service proposals

1.0.93

* Facet support

1.0.92

* added "insert parameter" dialog

1.0.91

* update to snapshot versioning

1.0.90

* merged #141 (faster builds)

1.0.87

* updated twig plugin

1.0.86

* fixed psr-2 namespace formatting bug

1.0.85

* added Symfony 2.1.2
* updated snakeyml library
* fixed builder setup during project creation
* fixed custom distribution setup
* fixed code template

1.0.84

* added Symfony 2.1.0

1.0.83

* updated to new composer implementation

1.0.82

* fixes #120

1.0.81

* fixed included composer feature

1.0.80

* fixed namespace resolver

1.0.79

* updated to Symfony 2.0.17 / 2.1 RC2

1.0.78

* updated to Symfony 2.0.16 / 2.1.RC1

1.0.77

* fixed route indexing for annotations

1.0.76 

* fixed custom project layout

1.0.75

* twig bugfix update

1.0.74

* updated twig 

1.0.73

* implemented twig template resolver

1.0.72

* updated to Symfony 2.0.15
* minor twig templateprovider performance improvement

1.0.71

* moved to composer/lucene indexing

1.0.70

* fixed service/route keybindings
* added missing service-class validator/quickfix


1.0.69

* added pattern and parameters to route completionproposal
* added "insert service ID" and "insert route" dialogs
* fixed selection-engine for controller annotations
* fixed route proposals in twig templates
* improved autocompletion in "new twig template" dialog
* updated to Symfony 2.0.14


1.0.68

* updated to Symfony 2.0.12 
* updated to composer-plugin 0.0.2
* minor npe fixes in namespace resolver

1.0.67

* added support for subpaths in viewpaths, e.g. MyBundle:Foo/Bar:index.html.twig

1.0.66

* added right-click menu for services in service-view
* added exclusion pattern for service indexing so test-services won't be indexed anymore
* fixed bug where services where shown multiple times in service view
* fixed asset hyperlinking to bundle folder instead of web/bundles/...
* fixed a couple of NPEs during indexing

1.0.63/1.0.64

* added composer nature to project wizard

1.0.62

* added composer (autoloading support)

1.0.61

* tycho p2 build integration

1.0.59
* removed beta flag
* removed pdt-dev dependency

1.0.58

* merged new annotation parser from maoueh => https://github.com/pulse00/Symfony-2-Eclipse-Plugin/pull/91

1.0.57

* updated to Symfony 2.0.11

1.0.56

* fixed #86

1.0.55

* fixed #83
* fixed duplicate twig codeassist proposals
* fixed use statement injection in twig templates

1.0.54

* updated to Symfony 2.0.10

1.0.53

* implemented twig extension point to auto-generate twig blocks from parent template
* moved "Add symfony nature" menu to "Configure..." submenu

1.0.52

* updated to 2.0.9

1.0.51

* improved feature to fully include dependencies

1.0.50

* version bump for updatesite

1.0.49

* updated to Symfony 2.0.8

1.0.48

* updated to p2 repository

1.0.47

* fixed cache folder for project wizard (#63)
* fixed duplicate codeassist proposals
* fixed injection of namespace into PDT class wizard

1.0.46

* added a Symfony code-formatter profile

1.0.45

* refactored class + getter/setter generation to PDT Extensions plugin
* refactored updatesite to host Symfony + Yedit + PDT Extensions features
* Prepared Yaml/XML Hyperlink targets
* updated built-in symfony to 2.0.6

1.0.44

* added type-inference for `getContainer` service calls
* added higher ranking for service proposals in case of route/service ambiguity

1.0.43

* fixed service completion inside commands
* improved service completion performance

1.0.42

* fixed project initialization bug (generator/skeleton exclude folder not set properly in buildpath)
* fixed non-symfony projects in services view

1.0.41

* Added Service view (Window -> Show View -> Other -> Symfony -> Services)

1.0.40

* Added Symfony 2.0.5
* Updated to new updatesite version

1.0.39

* Added template variable linking

1.0.38

* added debugger support

1.0.37

* added doctrine support
* improved annotation assist
* refactored annotation logic to separate plugin

1.0.36

* Updated to symfony 2.0.4
* Codeassist performance improvements

1.0.35

* Added hyperlinks for assets
* Added "PHP Class" wizard
* Added "New Symfony Project" wizard
* Added validation for implemented Interface methods
* Couple of bugfixes

1.0.32

* support for various twig improvements

1.0.31

* fixed bug in template variable codeassist

1.0.30

* added "Open Declaration" F3 support for @Template annotations
* added F3 support for Routes and Services

1.0.29

* added "Open Declaration" (F3) feature

1.0.28

* added support for viewpath completion in twig templates
* added hyperlink support for root-viewpaths ("::base.html.twig")
* improved proposal info for viewpaths
* improved viewpath completion (base paths now supported - "Bundle::|" and "::|")

1.0.26

* added hyperlinks for viewpaths and routes
* added custom proposal view for codeassist info (routes and services)
* added hover info for routes

1.0.25

* fixed annotation validation false positive

1.0.24

* improved route completion context

1.0.23

* added project homepage
* improved twig support
* improved service/route indexing
* fixed issue with index-schema deployment
* added file_link_format applescript for OSX
* fixed annotation highlighting
* improved service hyperlink detection
* added property page for synthetic services
* moved info from readme to project page


1.0.2

* added twig support feature

1.1.0

* annotation support moved to doctrine plugin
