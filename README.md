# taste.zip.server

## Description

크리에이터가 올린 유튜브 영상 리뷰들을 모아볼 수 있는 지도 어플리케이션을 통해, 크리에이터는 리뷰를 통해 조회수를 얻을 수 있는 기회를, 일반 사용자는 영상 리뷰를 통해 네이버/카카오 지도보다 더 정확하고 빠르게 맛집 정보를 탐색할 수 있는 편의성을 제공할 수 있다.

## Contiributors

|                                           민병록                                            |                                            이의제                                            |
|:----------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------:|
| <img width="300" alt="image" src="https://avatars.githubusercontent.com/u/96538554?v=4"> | <img width="300"  alt="image" src="https://avatars.githubusercontent.com/u/12531340?v=4"> | 
|                        [devrokket](https://github.com/devrokket)                         |                             [euije](https://github.com/euije)                             |
|               프로젝트 세팅, DB 설계, Entity 개발, 투표, 음식점 검색, 음식점 상세 조회, 이상형 월드컵 설계               |                                 DB 설계, 로그인/회원가입, 기타 작업 수행                                 |


## ⭐ Stack

<img src="https://img.shields.io/badge/Spring-6DB33F?style=flat-square&logo=Spring&logoColor=white"/>
<img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=flat-square&logo=Spring Boot&logoColor=white"/>
<img src="https://img.shields.io/badge/Java-137CBD?style=flat-square&logo=Java&logoColor=white"/>
<img src="https://img.shields.io/badge/PostgreSQL-4479A1?style=flat-square&logo=PostgreSQL&logoColor=white"/>

## 📌 Server architecture

<img width="630" alt="image" src="https://github.com/PiLab-CAU/OpenSourceProject-2401/assets/12531340/394a559f-dac7-4492-acea-fb21e33aaf70">

## ☘️Project Foldering

```
.
├── auth
│   └── 인증/인가와 관련된 모듈을 관리합니다.
│   └── JWT Token, OAuth 2.0 등의 주제가 이에 포함됩니다.
├── config
│   └── 백엔드 서버의 설정 값을 관리합니다.
│   └── Database, Seed, Documentation, Cors등이 이에 포함됩니다.
├── controller
│   └── REST API의 Endpoint 및 Specification를 구현합니다.
├── dto
│   └── REST API Specification의 요청, 응답 객체를 정의합니다.
├── entity
│   └── E-R Diagram의 Entity를 구현합니다.
├── repository
│   └── Entity의 저장소를 구현합니다.
│   └── JPA를 사용하여 구현합니다.
├── service
│   └── 서비스의 비즈니스 로직을 구현합니다.
└── TasteZipApplication
```

