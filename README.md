
# Headlines

A news app that fetches articles from 3 sources.

## API Reference

* [DataBinding](https://developer.android.com/topic/libraries/data-binding)

#### Get all news from [Guardian API](https://content.guardianapis.com)

```http
  GET /search
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `api-key` | `string` | **Required**. Your API key |

#### Get all news articles from [NEWS API](https://newsapi.org/v2)

```http
  GET /top-headlines
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `apiKey`      | `string` | **Required**. Your API key |
| `country`      | `string` | **Optional**. The 2-letter ISO 3166-1 code of the country you want to get headlines for. |
| `category`      | `string` | **Optional**. The category you want to get headlines for. Possible options: bussiness, entertainment, general, health, science, sports, technology. Note: you can't mix this param with the sources param. |
| `sources`      | `string` | **Optional**. A comma-separated string of identifiers for the news sources or blogs you want headlines from. Use the /sources endpoint to locate these programmatically or look at the sources index. Note: you can't mix this param with the country or category params |
| `q`      | `string` | **Optional**. Keywords or a phrase to search for. |
| `pageSize`      | `int` | **Optional**. The number of results to return per page (request). 20 is the default, 100 is the maximum. |
| `page`      | `int` | **Optional**. Use this to page through the results if the total results found is greater than the page size. |

#### Get all news articles from [New York Times API](https://api.nytimes.com)

```http
  GET /svc/topstories/v2/home.json
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `api-key`      | `string` | **Required**. Your API key |


## Requirements

* compileSdkVersion 33
* minSdkVersion 15
* targetSdkVersion 33
    
## Screenshots

![Home (in dark/night mode)](https://github.com/Detective-Khalifah/Headli9es/assets/58272349/7f0d4b1b-eb50-4faa-92bf-c2f9efe61ac7)
![Home (in light/day mode)](https://github.com/Detective-Khalifah/Headli9es/assets/58272349/bd17a03b-7065-4664-82fb-5f9848b2104f)
![NEWS API selected as outlet](https://github.com/Detective-Khalifah/Headli9es/assets/58272349/1a785567-9c01-4d82-81b3-c35a010e8836)
![Settings Activity](https://github.com/Detective-Khalifah/Headli9es/assets/58272349/8e4b8591-ad37-48d9-9c06-cd85a76cef1e)
