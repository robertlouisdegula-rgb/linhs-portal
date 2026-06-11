# TODO

## Plan confirmation
- [ ] Identify why adviser dashboard buttons fail (UI/JS vs backend).
- [ ] Fix root cause so Adviser initial accounts and Admin-created accounts both work.

## Implementation steps
- [ ] Update `adviser-dashboard.html` tab JS to not break Bootstrap/modal interactions.
- [ ] Ensure modal/form submissions work in all tabs (Add student, Add subject, Encode grades).
- [ ] Add a small guard to avoid JS errors from missing elements.

## Verification
- [ ] Test with initial adviser account: add student + add subject + delete subject.
- [ ] Test with admin-created adviser account: same flows.
- [ ] Run `mvn test` or `mvn -q test`.

