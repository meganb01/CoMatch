// Filter.js
export function createFilter(containerId, countries, sectors, skills, onFilterChange) {
  const container = document.getElementById(containerId);
  container.innerHTML = '';

  // Country dropdown
  const countryLabel = document.createElement('label');
  countryLabel.textContent = 'Country:';
  const countrySelect = document.createElement('select');
  countrySelect.id = 'country';
  countries.forEach(c => {
    const option = document.createElement('option');
    option.value = c;
    option.textContent = c;
    countrySelect.appendChild(option);
  });

  // Sector dropdown
  const sectorLabel = document.createElement('label');
  sectorLabel.textContent = 'Sector:';
  const sectorSelect = document.createElement('select');
  sectorSelect.id = 'sector';
  sectors.forEach(s => {
    const option = document.createElement('option');
    option.value = s;
    option.textContent = s;
    sectorSelect.appendChild(option);
  });

  // Skills buttons
  const skillsContainer = document.createElement('div');
  skillsContainer.classList.add('filter-container');
  skills.forEach(skill => {
    const button = document.createElement('button');
    button.textContent = skill;
    button.classList.add('skill-btn');
    button.dataset.skill = skill;
    skillsContainer.appendChild(button);

    button.addEventListener('click', () => {
      button.classList.toggle('selected');
      const selectedSkills = Array.from(
        skillsContainer.querySelectorAll('.skill-btn.selected')
      ).map(b => b.dataset.skill);

      onFilterChange({
        country: countrySelect.value,
        sector: sectorSelect.value,
        skills: selectedSkills
      });
    });
  });

  // Append elements
  container.appendChild(countryLabel);
  container.appendChild(countrySelect);
  container.appendChild(sectorLabel);
  container.appendChild(sectorSelect);
  container.appendChild(skillsContainer);

  function applyFilter() {
    const selectedSkills = Array.from(
      skillsContainer.querySelectorAll('.skill-btn.selected')
    ).map(b => b.dataset.skill);

    onFilterChange({
      country: countrySelect.value,
      sector: sectorSelect.value,
      skills: selectedSkills
    });
  }

  countrySelect.addEventListener('change', applyFilter);
  sectorSelect.addEventListener('change', applyFilter);
}