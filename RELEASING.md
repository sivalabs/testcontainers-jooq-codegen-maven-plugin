# Release process

The release process is automated through GitHub Actions. 
This describes the basic steps for a project member to perform a release.

## Steps

1. Ensure that the `main` branch is building and that tests are passing.
2. Create a new tag with the format `vX.Y.Z`.
3. The tag triggers a GitHub Action workflow.
4. JReleaser will perform the release to Maven Central
