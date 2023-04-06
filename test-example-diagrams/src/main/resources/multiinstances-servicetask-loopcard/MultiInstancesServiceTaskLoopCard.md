# Multi Instance Service Task Loop Cardinality

## Definition
Four multi instance task are created, with all combinations:



| Name                   | use case               | Activity executed | Activity waiting    |
|------------------------|------------------------|-------------------|---------------------|
| cardinality-parallel   | Cardinalité 17         | 2 executed        | 15 actives          |
| cardinality-sequential | Cadrinatity 9          | 4 executed        | 1 active, 4 futures |

## Expectation

Two tasks actives, and some task instances are already executed

## Diagram
![alt text](MultiInstancesServiceTaskLoopCard.png "Multi instances service task Loop Cardinality")

## Follow up


| Date          | Who                | Status                                             |
|---------------|--------------------|----------------------------------------------------|
| Feb 21, 2023  | Pierre-Yves Monnet | Definition                                         |
| March 9, 2023 | Pierre-Yves Monnet | Split to create Loop Card and Collection (2 tests) |
