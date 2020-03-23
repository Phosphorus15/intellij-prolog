Intellij Prolog
---
[![CI](https://github.com/Phosphorus15/intellij-prolog/workflows/CI/badge.svg)](https://github.com/Phosphorus15/intellij-prolog/actions)
![CircleCI](https://img.shields.io/circleci/build/github/Phosphorus15/intellij-prolog?logo=circleci)
[![][d-svg]][jb-url]
[![][v-svg]][jb-url]

 [d-svg]: https://img.shields.io/jetbrains/plugin/d/13954.svg
 [v-svg]: https://img.shields.io/jetbrains/plugin/v/13954.svg
 [jb-url]: https://plugins.jetbrains.com/plugin/13954

Prolog language support for Intellij IDEA.

### Road Map
- [ ] Basic language features
    - [x] Simple code highlighting
    - [x] Language file detection
    - [x] References and declarations linking
        - [x] Function(facts) references
        - [x] Atom(variable) references
    - [x] Code completion
        - [x] Function(facts) references
        - [x] Atom local references
    - [x] Goto symbol contributor
    - [ ] Internal analysis and linter
- [ ] Module and project management
    - [x] Cross file indexing
    - [ ] Standard library indexing
    - [ ] ?: External library indexing
- [ ] Swi toolchain support
    - [x] Auto detection & configuration
    - [x] Basic external linter
    - [ ] Case dependent external linter
    - [x] Run configurations
- [x] Icons needed
- [ ] Pattern unification
- [ ] Advanced: interactive query & debugging
