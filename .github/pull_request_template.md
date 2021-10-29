Many thanks for submitting your Pull Request :heart:! 

Please make sure that your PR meets the following requirements:

**WARNING! Please make sure you are opening your PR against `main` branch!**

- [ ] You have read the [contributors guide](https://github.com/kiegroup/kogito-runtimes#contributing-to-kogito)
- [ ] Pull Request title is properly formatted: `KOGITO-XYZ Subject`
- [ ] Pull Request title contains the target branch if not targeting main: `[0.9.x] KOGITO-XYZ Subject`
- [ ] Pull Request contains link to the JIRA issue
- [ ] Pull Request contains link to any dependent or related Pull Request
- [ ] Pull Request contains description of the issue
- [ ] Pull Request does not include fixes for issues other than the main ticket

<details>
<summary>
How to replicate CI configuration locally?
</summary>

We do "simple" maven builds, they are just basically maven commands, but just because we have multiple repositories related between them and one change could affect several of those projects by multiple pull requests, we use [build-chain tool](https://github.com/kiegroup/github-action-build-chain) to handle cross repository builds and be sure that we always use latest version of the code for each repository.

[build-chain tool](https://github.com/kiegroup/github-action-build-chain) is not only a github-action tool but a CLI one, so in case you posted multiple pull requests related with this change you can easily reproduce the same build by executing it locally. See [local execution](https://github.com/kiegroup/github-action-build-chain#local-execution) details to get more information about it.
</details>

<details>
<summary>
How to retest this PR or trigger a specific build:
</summary>

* <b>Pull Request</b>  
  Please add comment: <b>Jenkins retest this</b>
 
* <b>Quarkus LTS checks</b>  
  Please add comment: <b>Jenkins run LTS</b>

* <b>Native checks</b>  
  Please add comment: <b>Jenkins run native</b>

</details>